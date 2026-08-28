import javax.sound.midi.*;

public class SoundManager {
    private Synthesizer synth;
    private MidiChannel channel;

    public SoundManager() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            channel = synth.getChannels()[4]; // Percussion/Synth channel
        } catch (Exception ignored) {}
    }

    public void playKeyPressSound() {
        if (channel != null) {
            channel.noteOn(72, 60); // Soft click sound
        }
    }

    public void playErrorSound() {
        if (channel != null) {
            channel.noteOn(36, 100); // Low error thump
        }
    }
}