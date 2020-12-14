import bpy
bpy.context.scene.cycles.device = 'GPU'
#bpy.ops.render.render(True)  //sets rendersettings equals to those in the .blendfile --> unwanted because overrides settings made in NetworkAPK