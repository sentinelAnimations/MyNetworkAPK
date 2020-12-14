import bpy
bpy.context.scene.cycles.device = 'CPU'
#bpy.ops.render.render(True)   //sets rendersettings equals to those in the .blendfile --> unwanted because overrides settings made in NetworkAPK

for scene in bpy.data.scenes:
    scene.render.tile_x = 32
    scene.render.tile_y = 32