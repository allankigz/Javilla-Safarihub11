import { initializeApp } from "firebase-admin/app";
import { getDatabase } from "firebase-admin/database";
import { getMessaging } from "firebase-admin/messaging";
import { onValueCreated, onValueWritten } from "firebase-functions/v2/database";
import { logger, setGlobalOptions } from "firebase-functions";

initializeApp();

setGlobalOptions({
  region: "us-central1",
  maxInstances: 10,
});

const db = getDatabase();

/**
 * Rebuilds the aggregate helpful count from the idempotent per-user vote set.
 * Rebuilding instead of incrementing makes retries safe.
 */
export const syncReviewHelpfulCount = onValueCreated(
  "reviewHelpful/{reviewId}/{uid}",
  async (event) => {
    const reviewId = event.params.reviewId;
    const votes = await db.ref(`reviewHelpful/${reviewId}`).get();
    const count = votes.exists() ? Object.keys(votes.val() as Record<string, unknown>).length : 0;
    await db.ref(`reviews/${reviewId}/helpfulCount`).set(count);
  },
);

/** Rebuild scam confirmations from the unique per-user confirmation set. */
export const syncScamConfirmationCount = onValueCreated(
  "scamReportConfirmations/{reportId}/{uid}",
  async (event) => {
    const reportId = event.params.reportId;
    const confirmations = await db.ref(`scamReportConfirmations/${reportId}`).get();
    const count = confirmations.exists()
      ? Object.keys(confirmations.val() as Record<string, unknown>).length
      : 0;
    await db.ref(`scamReports/${reportId}/confirmedCount`).set(count);
  },
);

/** Rebuild safety-incident confirmations from the unique per-user set. */
export const syncSafetyConfirmationCount = onValueCreated(
  "safetyIncidentConfirmations/{incidentId}/{uid}",
  async (event) => {
    const incidentId = event.params.incidentId;
    const confirmations = await db.ref(`safetyIncidentConfirmations/${incidentId}`).get();
    const count = confirmations.exists()
      ? Object.keys(confirmations.val() as Record<string, unknown>).length
      : 0;
    await db.ref(`safetyIncidents/${incidentId}/confirmedCount`).set(count);
  },
);

/**
 * Sends an approved safety incident to the safety_alerts topic.
 * Pending/rejected incidents never generate an alert.
 */
export const notifyApprovedSafetyIncident = onValueWritten(
  "safetyIncidents/{incidentId}",
  async (event) => {
    const before = event.data.before.val() as Record<string, unknown> | null;
    const after = event.data.after.val() as Record<string, unknown> | null;
    if (!after) return;

    const becameApproved = after.moderationStatus === "approved" && before?.moderationStatus !== "approved";
    if (!becameApproved) return;

    const title = `Javilla Safety Alert: ${String(after.category ?? "Safety incident")}`;
    const body = `${String(after.location ?? "Kenya")}: ${String(after.description ?? "A safety incident was reported.")}`.slice(0, 450);

    await getMessaging().send({
      topic: "safety_alerts",
      notification: { title, body },
      data: {
        type: "safety_alert",
        incidentId: event.params.incidentId,
        location: String(after.location ?? ""),
      },
      android: { priority: "high" },
    });

    logger.info("Safety alert sent", { incidentId: event.params.incidentId });
  },
);

/** Notify a traveller when an administrator confirms or rejects their booking. */
export const notifyBookingStatus = onValueWritten(
  "bookings/{bookingId}",
  async (event) => {
    const before = event.data.before.val() as Record<string, unknown> | null;
    const after = event.data.after.val() as Record<string, unknown> | null;
    if (!after) return;

    const status = String(after.status ?? "pending");
    if (status === String(before?.status ?? "pending")) return;
    if (status !== "confirmed" && status !== "rejected") return;

    const uid = String(after.userId ?? "");
    if (!uid) return;

    const tokenSnapshot = await db.ref(`users/${uid}/fcmTokens`).get();
    const tokens = tokenSnapshot.exists()
      ? Object.keys(tokenSnapshot.val() as Record<string, unknown>)
      : [];
    if (tokens.length === 0) return;

    const title = status === "confirmed" ? "Booking confirmed" : "Booking update";
    const body = status === "confirmed"
      ? `${String(after.service ?? "Your service")} is confirmed for ${String(after.date ?? "your selected date")}.`
      : `${String(after.service ?? "Your booking")} was not confirmed. Please review your booking options.`;

    const result = await getMessaging().sendEachForMulticast({
      tokens,
      notification: { title, body },
      data: {
        type: "booking_status",
        bookingId: event.params.bookingId,
        status,
      },
    });

    // Remove invalid/expired tokens so future sends remain efficient.
    const removals: Record<string, null> = {};
    result.responses.forEach((response, index) => {
      const code = response.error?.code ?? "";
      if (code.includes("registration-token-not-registered") || code.includes("invalid-registration-token")) {
        removals[`users/${uid}/fcmTokens/${tokens[index]}`] = null;
      }
    });
    if (Object.keys(removals).length > 0) await db.ref().update(removals);
  },
);
