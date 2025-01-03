### Adds a new Command `/morph` & `/unmorph`:

- `/morph <morphPlayer> <morphIntoTarget> [<key>]` (Changes the morphPlayer's skin to the morphIntoTarget's one while not changing it in the PlayerTabOverlay and SocialInteractionPlayerList)
- `/unmorph <morphedPlayer> [<key>]` (Changes the morphedPlayer's skin back to the correct one (same as /morph with morphPlayer, morphIntoTarget and key being the same). Instead of the key `any` can be set, which removes all morphs)

Optionally, a key (number) can be set on both commands,
which allows multiple morphs that are applied again after unmorphing one (Setting no key sets the key -1).
The morph that is applied is always the last one that has been added.
For example (active morphs in order of application → shown one):
- 1, 3, 2 → 2
- 1, 3 → 3 (`/unmorph <player> 2`)
- 3 → 3 (`/unmorph <player> 1`)

**Beware that morphing the skin is handled on the client and could therefore be exploited by someone who is familiar with modding!**

### Adds a new Command `/glow`:

- `/glow <target> add <viewers>` (Adds a glow to the target for the specified viewers)
- `/glow <target> remove <viewers>` (Removes the glow on the target for the specified viewers)
- `/glow <target> clear` (Removes the glow on the target for all active viewers)

**The vanilla glow (Attribute / Effect) overwrites the glow created by this command!**