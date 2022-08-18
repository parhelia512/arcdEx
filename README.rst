==========================================
arcdEx - Defold .arcd file extraction tool
==========================================

`Defold game engine`__ archive v4 file extractor.

__ https://defold.com/


Features
========

- List ``.arcd`` contents
- Extract files from ``.arcd``. Compressed and encrypted files supported.
- Inspect ``.texturec`` files
- Extract ``.texturec`` files
- Inspect ``.luac`` files (compiled Lua files)
- Extract ``.luac`` Lua source code

Decryption is only supported when the default Defold encryption key is used.


Usage
=====
Three files need to be available:

``game.arcd``
  Game archive file containing the actual data
``game.arci``
  Game archive index file containing the data offsets
``game.dmanifest``
  Manifest file containing the file names

Only the ``.arcd`` file needs to be passed, the other paths are guessed.

The web version of Defold games often use multiple ``.arcd`` files.
They have to be combined before they can be used with arcdEx::

    $ cat game.arcd0 game.arcd1 > game.arcd


Archive
-------
List archive contents::

    $ arcdEx archive -l game.arcd
    Flags: C=compressed, E=encrypted, L=liveupdate
          Size Compressed Flags Filename
            90         -1       /_generated_2e3b32552c42f133.spritec
          5044       1136       /assets/bundle/common/levels/result/1_easy_2.json
          1848        669  E    /world/game/ecs/systems/button_push_system.luac

Extract all files from the archive::

    $ arcdEx archive game.arcd --outdir=extract -v
    Extracting "/scenes/win_scene/win_scene.luac" to "/home/user/extract/scenes/win_scene/win_scene.luac"

Extract a single file from the archive::

    $ arcdEx archive --outdir=extract -v game.arcd /scenes/win_scene/win_scene.luac
    Extracting "/scenes/win_scene/win_scene.luac" to "/home/user/extract/scenes/win_scene/win_scene.luac"

Extract a ``.texturec`` and all textures contained inside::

    $ arcdEx archive -v --extract-textures --outdir=extract test.arcd /assets/images/game/game.texturec
    Extracting "/assets/images/game/game.texturec" to "/home/user/extract/assets/images/game/game.texturec"
     Extracting texturec file: /home/user/extract/assets/images/game/game.texturec
      Writing game.texturec-0


Extract a ``.luac`` and the Lua source code inside it::

    $ arcdEx archive -v --extract-lua --outdir=extract test.arcd /scenes/win_scene/win_scene.luac
    Extracting "/scenes/win_scene/win_scene.luac" to "/home/user/extract/scenes/win_scene/win_scene.luac"
     Writing /home/user/extract/scenes/win_scene/win_scene.lua


Lua scripts
-----------
Show information about a ``.luac`` file::

    $ arcdEx lua -i extract/libs_project/cameras.luac
    Filename: libs_project/cameras.lua
    Script size: 1006
    Required modules:
    - libs.common
    - libs.rendercam_camera
    Required resources:
    - /libs/common.luac
    - /libs/rendercam_camera.luac

Extract the Lua source code from a ``.luac`` file::

    $ arcdEx lua -v extract/libs_project/cameras.luac
    Writing /home/user/extract/libs_project/cameras.lua

    $ head -n2 extract/libs_project/cameras.lua
    local LEVELS = require "world.game.levels.levels"
    local WORLD = require "world.world"


Textures
--------
Show information about a ``.texturec`` file::

    $ arcdEx texture -i extract/assets/images/game/game.texturec
    Number of alternatives: 1
    Type: 2D
    Alternative #0
     Size: 1024x512
     Original size: 1024x512
     Format: RGBA
     Compression: basis UASTC
     Compression flags: 0

Extract texture files::

    $ arcdEx texture -v extract/assets/images/game/game.texturec
    Extracting texturec file: /home/user/extract/assets/images/game/game.texturec
     Writing game.texturec-0

Texture data files often are in a format that can be directly uploaded
to the graphics card as a texture.
When compressed with "basis UASTC" they can be converted into a ``.png`` file
with the `basis_universal`__ ``basisu`` tool::

    $ basisu -unpack -no_ktx -file extract/assets/images/game/game.texturec-0
    Basis Universal GPU Texture Compressor v1.16.3
    Copyright (C) 2019-2022 Binomial LLC, All rights reserved
    Using SSE 4.1: 1, Multithreading: 1, Zstandard support: 1, OpenCL: 0
    Input file "extract/assets/images/game/game.texturec-0", KTX2: 0
    File version and CRC checks succeeded
    File info:
      Version: 13
      Texture format: UASTC
      Texture type: 2D
      Total slices: 1
      Total images: 1
    ...
    Transcode of image 0 level 0 res 1024x512 format UASTC_4x4 succeeded in 0.067 ms
    Wrote PNG file "game_unpacked_rgb_UASTC_4x4_0000.png"
    Wrote PNG file "game_unpacked_a_UASTC_4x4_0000.png"

__ https://github.com/BinomialLLC/basis_universal



============
About arcdEx
============
arcdEx was written by `Christian Weiske`__ and is licensed under the
`AGPL v3`__.

It uses some parts of the Defold game engine source code, especially the ArchiveReader
and the protocol buffer source files.

__ https://cweiske.de/
__ https://www.gnu.org/licenses/agpl-3.0.en.html
