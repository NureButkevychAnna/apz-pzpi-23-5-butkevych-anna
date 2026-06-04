try {
  const Subscription = require("./Subscription");
  const redis = require("./redisClient");

  const CACHE_KEY = "all_subscriptions";
  const CACHE_TTL = 60;

  async function clearCache() {
    try {
      await redis.del(CACHE_KEY);
      console.log("DEBUG: Subscription cache cleared.");
    } catch (error) {
      console.error("Cache clear error:", error);
    }
  }

  async function getAllSubscriptions() {
    try {
      const cached = await redis.get(CACHE_KEY);
      if (cached) return cached;
    } catch (e) {
      console.error("Redis get error in getAllSubscriptions:", e);
    }

    const rows = await Subscription.findAll();

    try {
      await redis.setEx(CACHE_KEY, CACHE_TTL, rows);
    } catch (e) {
      console.error("Redis set error in getAllSubscriptions:", e);
    }

    return rows;
  }

  async function getSubscriptionById(id) {
    return Subscription.findByPk(id);
  }

  async function addSubscription(data) {
    const rec = await Subscription.create(data);
    await clearCache();
    return rec;
  }

  async function updateSubscription(id, changes) {
    await Subscription.update(changes, { where: { id } });
    await clearCache();
    return Subscription.findByPk(id);
  }

  async function removeSubscription(id) {
    const res = await Subscription.destroy({ where: { id } });
    await clearCache();
    return res;
  }

  console.log("DEBUG: subscriptionModel initialized.");

  module.exports = {
    getAllSubscriptions,
    getSubscriptionById,
    addSubscription,
    updateSubscription,
    removeSubscription,
  };
} catch (error) {
  console.error(
    "FATAL ERROR IN subscriptionModel INITIALIZATION:",
    error.message,
  );
  process.exit(1);
}
