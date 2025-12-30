package de.one_piece_content.network;

import de.one_piece_content.ExampleMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SandSpikePayload(int entityId) implements CustomPayload {
    public static final CustomPayload.Id<SandSpikePayload> ID = new CustomPayload.Id<>(
            ExampleMod.id("sand_spike_anim"));
    public static final PacketCodec<RegistryByteBuf, SandSpikePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, SandSpikePayload::entityId,
            SandSpikePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
