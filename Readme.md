# Pixelmon Generations Bingo
## Development Setup
### Dependencies
Put the dependency jars that aren't accessible from any public repository
into the `libs` folder. This includes, but isn't necessarily limited to the
pixelmon generations mod itself.

Pull the access transformers from the pixelmon generations jar 
(META-INF/pixelmon_at.cfg) and also place them into the `libs` folder.
## Development
### New task types
Create a new class extending BingoTask and handle it in all the places 
annotated with `//TaskTypeSwitch`.