package firis.mobbottle.common.helpler;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class VanillaNetworkHelper {
	/**
	 * TileEntityのパケットを送信する
	 * @param tile
	 */
	public static void sendPacketTileEntity(TileEntity tile) {
		//player listを取得
		World world = tile.getWorld();
		//サーバの場合のみ
		if (!world.isRemote) {
			List<EntityPlayer> list = world.playerEntities;
			Packet<?> pkt = tile.getUpdatePacket();
			tile.markDirty();
			if (pkt != null) {
				for (EntityPlayer player : list) {
					EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
					mpPlayer.connection.sendPacket(pkt);
				}
			}
		}
	}
}