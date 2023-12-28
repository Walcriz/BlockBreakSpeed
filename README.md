# BlockBreakSpeed
A **semi** drop-in replacement for **Breaker2** that uses potion effects to minimize desync/packets sent between server and client.

Tested versions: 1.19.4

> :warning: This is ARCHIVED and will not be updated. The code *works* but the limitations of this method were too big to even consider using

## Benefits
- It sends way less packets to the client (*the reason this was made*)
- Reduces chances of desync between server and client (*Needs more testing*)

## Drawbacks
- Since BlockBreakSpeed relies on potion effects it **cannot** make unbreakable blocks breakable.
- This plugin requires **ProtocolLib** to function
- It does not include all features that **Breaker2** offers. It is very limited in scope. Such features may include:
  - **Exact** break time (*It will estimate*)
  - All state state/trigger providers supported by **Breaker2**
