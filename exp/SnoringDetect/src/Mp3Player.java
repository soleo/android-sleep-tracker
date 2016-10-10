
import javax.media.*;
import java.io.*;
import java.util.*;
public class Mp3Player implements ControllerListener{
	//Player object
	private Player player;
	//whether play in loop
	private boolean first, loop;
	//file path
	private String path;
	//Mp3 files list
	private List<String> mp3List;
	//Count of Mp3 files
	private int mp3NO=0;
	
	Mp3Player(List<String> mp3List){
		this.mp3List=mp3List;
	}
	
	//Play method
	public void start(){
		try{
			player = Manager.createPlayer(new File(mp3List.get(mp3NO)).toURI().toURL());
		}catch(NoPlayerException e){
			e.printStackTrace();
			System.out.println("Cannot Play This List!");
			return;
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
		if(player==null){
			System.out.println("Cannot Play Empty List!");
			return;
		}
		player.addControllerListener(this);
		player.prefetch();
	}
	
	public void controllerUpdate(ControllerEvent e){
		if(e instanceof EndOfMediaEvent){
			mp3NO++;
			System.out.println(mp3NO);
			if(mp3NO<mp3List.size()){
				this.start();
			}
			return;
		}
		if(e instanceof PrefetchCompleteEvent){
			System.out.println("Playing to the End");
			player.start();
			return;
		}
		if(e instanceof RealizeCompleteEvent){
			System.out.println("Realization");
			return;
		}
	}
	
	public static void main(String[] args){
		List<String> path = new ArrayList<String>();
		path.add("/Users/edmond/Documents/audio/test01.mp3");
		Mp3Player play = new Mp3Player(path);
		play.start();
	}

}
