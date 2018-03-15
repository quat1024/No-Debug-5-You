package quaternary.nd5u;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod(modid = NoDebug5You.MODID, name = NoDebug5You.NAME, version = NoDebug5You.VERSION, clientSideOnly = true)
public class NoDebug5You {
	public static final String MODID = "nd5u";
	public static final String NAME = "No Debug 5 You";
	public static final String VERSION = "0.0.0";
	
	public static final Logger log = LogManager.getLogger(NAME);
	
	//I know this is a spooky internal event.
	//BUT! it's fired *after* postinit, so i'm sure to get all the event subscribers.
	@Mod.EventHandler
	public static void loadComplete(FMLLoadCompleteEvent e) {		
		MinecraftForge.EVENT_BUS.register(new Testing2());
		
		//Find the list of methods that are registered to RenderGameOverlayEvent.Text,
		//which is the event responsible for mods rendering things in f3
		//(Oddly enough getListenerList isn't static so I have to do a little song and dance)
		RenderGameOverlayEvent dummy = new RenderGameOverlayEvent(0, new ScaledResolution(Minecraft.getMinecraft()));
		RenderGameOverlayEvent.Text dummy2 = new RenderGameOverlayEvent.Text(dummy, new ArrayList<>(), new ArrayList<>());
		//I mean i know it's 0, but might as well be safe
		int eventBusID = ReflectionHelper.getPrivateValue(EventBus.class, MinecraftForge.EVENT_BUS, "busID");
		
		ListenerList textListeners = dummy2.getListenerList();
		IEventListener[] listeners = textListeners.getListeners(eventBusID);
		
		if(NoDebug5YouConfig.dump) {
			hr();
			log.info("Beginning dump of class names...");
			hr();
		}
		
		OperationMode operationMode = NoDebug5YouConfig.operationMode;
		List<String> list = Arrays.asList(NoDebug5YouConfig.list);
		
		//Now the weird part, converting functional interface *implementations* to strings (?!)
		//I genuinely don't know why this works or where this string comes from
		for(IEventListener listener : listeners) {
			String s = listener.toString();
			
			log.info("HYPER DEBUGGG " + s);
			
			if(!s.startsWith("ASM: ")) continue;
			if(!s.contains("RenderGameOverlayEvent$Text")) continue;
			
			log.info("good");
			
			//split on whitespace
			String[] split = s.split("\\s");
			
			String className;
			if(split[1].equals("class")) { //static event handler
				className = split[2];
			} else { //non-static; chop off the instance bit
				className = split[1].split("@")[0];
			}
			
			//String className = split[2];
			if(NoDebug5YouConfig.dump) log.info(className);
			
			if(operationMode == OperationMode.NOTHING) continue;
			if(operationMode == OperationMode.WHITELIST && !list.contains(className)) continue;
			if(operationMode == OperationMode.BLACKLIST && list.contains(className)) continue;
			
			if(NoDebug5YouConfig.dump) log.info("(The above class was unsubscribed due to your ND5U config options.)");
			
			textListeners.unregister(eventBusID, listener);
		}
		
		if(NoDebug5YouConfig.dump) {
			hr();
			log.info("That's all, folks!");
			hr();
		}
	}
	
	private static void hr() {
		log.info("===========================================================");
	}
	
	@Mod.EventBusSubscriber
	public static class Testing {
		@SubscribeEvent
		public static void text(RenderGameOverlayEvent.Text t) {
			t.getLeft().add("Im a banana!!!!!!");
		}
		
		@SubscribeEvent
		public static void chat(ClientChatEvent e) {
			log.info("Bam!");
		}
	}
	
	public static class Testing2 {
		@SubscribeEvent
		public void text(RenderGameOverlayEvent.Text t) {
			t.getLeft().add("Static");
		}
	}
}
