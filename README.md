****SafeGuard****

**Use case flow:**
1. Rental car speed limit - company sets the speed limit before each ride. 
2. Speed limit could be different for each customer.
3. Monitor for speed limit and if speed limit exceeds 
   a) send this event to rental company: Using firebase(will be replaced by AWS)
   b) Show warning alert to user.

**Assumptions**
1. The speed limit of each user is received via get api call.
2. The speed is calculated using locations api in the app.
3. Api calls are simulated and randomly returns rental info.
4. Speed monitoring system is mocked using fused location api.
5. Firebase and AWS notifications are stubbed.