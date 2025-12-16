package de.one_piece_content.registries;

import de.one_piece_content.ExampleMod;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class MySounds {
    public static final class Entry {
        private final Identifier id;
        private final SoundEvent soundEvent;
        public Entry(Identifier id, SoundEvent soundEvent) {
            this.id = id;
            this.soundEvent = soundEvent;
        }

        public Entry(String name) {
            this(ExampleMod.id(name));
        }

        public Entry(Identifier id) {
            this(id, SoundEvent.of(id));
        }

        public Identifier id() {
            return id;
        }

        public SoundEvent soundEvent() {
            return soundEvent;
        }
        public int variants() {
            return 1;
        }
    }
    public static final List<Entry> entries = new ArrayList<>();
    public static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static final Entry SHISHI_SONSON = add(new Entry("shishi_sonson"));
    public static final Entry YAKKODORI = add(new Entry("yakkodori"));

    public static void register() {
        /*for (var entry: entries) {
            entry.entry = Registry.registerReference(Registries.SOUND_EVENT, entry.id(), entry.soundEvent());
        }*/
    }
}
