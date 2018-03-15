package quaternary.nd5u;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod(modid = NoDebug5You.MODID, name = NoDebug5You.NAME, version = NoDebug5You.VERSION, clientSideOnly = true)
@Mod.EventBusSubscriber
public class NoDebug5You {
	public static final String MODID = "nd5u";
	public static final String NAME = "No Debug 5 You";
	public static final String VERSION = "0.0.0";
	
	public static final Logger log = LogManager.getLogger(NAME);
	
	static String HR = "========================================================";
	
	@Mod.EventHandler
	public static void postinit(FMLPostInitializationEvent e) {			
		doRemovals();
	}
	
	//catch mods that register their event handlers a bit late
	// *glares at OreCruncher
	static boolean loggedIn;
	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent e) {
		if(!loggedIn && Minecraft.getMinecraft().world != null) {
			doRemovals();
			loggedIn = true;
		}
		
		if(loggedIn && Minecraft.getMinecraft().world == null) {
			loggedIn = false;
		}
	}
	
	private static void doRemovals() {
		//Find the list of methods that are registered to RenderGameOverlayEvent.Text,
		//which is the event responsible for mods rendering things in f3
		//(Oddly enough getListenerList isn't static so I have to do a little song and dance)
		RenderGameOverlayEvent dummy = new RenderGameOverlayEvent(0, new ScaledResolution(Minecraft.getMinecraft()));
		RenderGameOverlayEvent.Text dummy2 = new RenderGameOverlayEvent.Text(dummy, new ArrayList<>(), new ArrayList<>());
		//I mean i know it's 0, but might as well be safe
		int eventBusID = ReflectionHelper.getPrivateValue(EventBus.class, MinecraftForge.EVENT_BUS, "busID");
		
		ListenerList textListeners = dummy2.getListenerList();
		IEventListener[] listeners = textListeners.getListeners(eventBusID);
		
		List<String> dumpList = new ArrayList<>();
		int classCount = 0;
		dumpList.add(HR);
		dumpList.add("Beginning dump of class names...");
		dumpList.add(HR);
		
		OperationMode operationMode = NoDebug5YouConfig.operationMode;
		List<String> list = Arrays.asList(NoDebug5YouConfig.list);
		
		for(IEventListener listener : listeners) {
			if(!(listener instanceof ASMEventHandler)) continue;
			
			String s = listener.toString(); //ASMEventHandler#readable basically
			
			if(!s.contains("RenderGameOverlayEvent$Text")) continue;
			
			//now parse this weird string to find the class name
			String[] split = s.split("\\s");
			
			String className;
			if(split[1].equals("class")) { //static event handler
				className = split[2];
			} else { //non-static event handler; chop off the instance thingie
				className = split[1].split("@")[0];
			}
			
			dumpList.add("Found a class: '" + className + "'");
			if(className.contains("{")) {
				dumpList.add("(I know that doesn't look like a class name, sorry about that! It will still work)");
			}
			classCount++;
			
			if(operationMode == OperationMode.NOTHING) continue;
			if(operationMode == OperationMode.WHITELIST && !list.contains(className)) continue;
			if(operationMode == OperationMode.BLACKLIST && list.contains(className)) continue;
			
			dumpList.add("(The above class was already unsubscribed due to your ND5U config options.)");
			
			textListeners.unregister(eventBusID, listener);
		}
		
		dumpList.add(HR);
		dumpList.add("A grand total of " + classCount + " class" + (classCount == 1 ? "." : "es."));
		dumpList.add("If you would like to disable these, add the class name to your NoDebug5You config file!");
		dumpList.add(HR);
		
		if(NoDebug5YouConfig.dump) {
			for(String line : dumpList) {
				log.info(line);
			}
		}
	}
}
