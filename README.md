# SkLectern

[![license](https://img.shields.io/github/license/kiip1/SkLectern?style=for-the-badge&color=dd7744)](./LICENSE)
[![wiki](https://img.shields.io/badge/documentation-wiki-x?style=for-the-badge&color=cc7788)](https://github.com/kiip1/SkLectern/wiki)

Supercharge your scripts with various new language features and performance gains, transpiling right into Skript code.

Please do note that this is just a proof of concept, and the result is by no means production grade.

## Installation
Go [here](https://github.com/kiip1/SkLectern/releases/tag/latest) for the latest release.
Install either the Bukkit version or CLI.

## Features
- [Macros](#macros)
- [Arithmetic](#arithmetic)

### Macros
This language feature is similar to Skript options, it gets searched and replaced before execution like options, but allows arguments like functions.
The benefit of macros is that they don't have a performance impact unlike functions, and it allows for syntax elements to be passed as argument.
Please do note that parsing times may increase, and your scripts can become quite large when transpiled. Use with caution and optimize the macro body.

Macros need to end with a ! and unlike functions they don't have types for arguments.

The syntax to use macros is as follows:
```vb
command /test:
  trigger:
    chance!(broadcast "hi", 0.5)

macro chance!(statement, chance):
  if chance of $chance:
    $statement
```
With as result:
```vb
command /test:
  trigger:
    if chance of 0.5:
      broadcast "hi"
```

Structures are also supported:
```vb
item!(dirt, 5 seconds)
item!(stone, 10 seconds)

structure macro item!(item, cooldown):
  command /$item:
    cooldown: $cooldown
    cooldown message: &cWait a little longer before getting another item!
    trigger:
      give $item to player
```
With as result:
```vb
command /dirt:
  cooldown: 5 seconds
  cooldown message: &cWait a little longer before getting another item!
  trigger:
    give dirt to player

command /stone:
  cooldown: 10 seconds
  cooldown message: &cWait a little longer before getting another item!
  trigger:
    give stone to player
```

### Arithmetic
Simple arithmetic like `3 + 2` gets simplified into `5`.
This allows you to write more clear code without worrying about performance.

## Goals
- Add more language features to Skript for advanced users.
- Optimize parse times without requiring manual optimization.
- Give high quality errors during transpiling process.
- Be strict in language usage, probable bugs should be reported as an error.

## Non-Goals
- Add syntax elements to Skript, like a Skript addon.
- Require end-users to depend on SkLectern.

## Contributing
Feel free to contribute, it might be smart to discuss features beforehand.
More syntax coverage is always welcome.

## Roadmap
- [x] Initial attempt with a proof of concept.
- [ ] Add implementations for CLI and Bukkit.
- [ ] Create API to allow for easier hooking.
- [x] Performance: Literal Arithmetic
- [ ] Language Feature: Annotations
- [ ] Performance: Pure Functions
- [ ] Language Feature: Auto-Cleaning Variables
- [ ] Language Feature: Conditional Events
