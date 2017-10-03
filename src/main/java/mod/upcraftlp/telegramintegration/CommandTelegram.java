package mod.upcraftlp.telegramintegration;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * (c)2017 UpcraftLP
 */
public class CommandTelegram extends CommandBase {

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if(args.length == 1) return getListOfStringsMatchingLastWord(args, "list", "add", "remove", "broadcast", "clear");
        else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("remove")) {
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "telegram";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/telegram\n" +
                "* add <ChatID>: add another chat ID\n" +
                "* remove <index>: remove a chat from the list\n" +
                "* list: list all connected chats\n" +
                "* clear: clear the chat list\n" +
                "* broadcast <message>: broadcast a message to all telegram chats";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if(args.length == 1) {
            String command = args[0];
            if(command.equalsIgnoreCase("list")) {
                String ret = "";
                for (int i = 0; i < Reference.TelegramConfig.chatIDs.length; i++) {
                    ret += TextFormatting.RED + "#" + (i + 1) + TextFormatting.RESET + " - " + TextFormatting.GREEN + Reference.TelegramConfig.chatIDs[i];
                    if(i < Reference.TelegramConfig.chatIDs.length - 1) ret += "\n";
                }
                sender.sendMessage(new TextComponentString(ret));
                return;
            }
            else if(command.equalsIgnoreCase("clear")) {
                TelegramHandler.clearList();
                sender.sendMessage(new TextComponentString("Successfully cleared the chat list!"));
                return;
            }
        }
        else if(args.length >= 2 && args[0].equalsIgnoreCase("broadcast")) {
            String message = args[1];
            for(int i = 2; i < args.length; i++) message += " " + args[i];
            TelegramHandler.postToAll("*" + sender.getName() + ":* " + message);
            sender.sendMessage(new TextComponentString("Broadcasted Message!"));
            return;
        }
        else if(args.length == 2) {
            String command = args[0];
            String arg = args[1];
            if(command.equalsIgnoreCase("add")) {
                if(!TelegramHandler.add(arg)) {
                    throw new CommandException(arg + " is already in the chat list!");
                }
                else sender.sendMessage(new TextComponentString("Successfully added " + arg + " to the chat list!"));
                return;
            }
            else if(command.equalsIgnoreCase("remove")) {
                if(!TelegramHandler.remove(parseInt(arg) - 1)) {
                    throw new CommandException("#" + arg + " does not exist, number must be at max "  + (Reference.TelegramConfig.chatIDs.length) + "!");
                }
                else sender.sendMessage(new TextComponentString("Successfully removed chat #" + arg + "!"));
                return;
            }
        }
        throw new WrongUsageException(this.getUsage(sender));
    }

}
