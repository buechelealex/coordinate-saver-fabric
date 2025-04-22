package org.pommes2864.test.test;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


import static net.minecraft.server.command.CommandManager.*;

public class Test implements ModInitializer {


    public static final String MOD_ID = "TestModPommes";
    private static ArrayList<Coordinates> ArrayListCoords = new ArrayList<Coordinates>();



    @Override
    public void onInitialize() {
        //Ordner fuer JSON dateien erstellen
        try {
            Files.createDirectories(Paths.get("./waypoints"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ImportJsonToArrayList();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("coord")
                .then(literal("list")
                        .executes(Test::ReturnAllCoordinates))

                .then(literal("add")
                        .then(argument("name", StringArgumentType.string())
                                .executes(Test::AddCoordinatesOfPlayerToArrayList)


                                .then(argument("posx", IntegerArgumentType.integer())
                                        .then(argument("posy", IntegerArgumentType.integer())
                                                .then(argument("posz", IntegerArgumentType.integer())
                                                        .executes(Test::AddCustomCoordinatesToArrayList)
                                                )
                                        )
                                )

                        )
                )
                .then(literal("delete")
                .then(argument("name", StringArgumentType.string())
                        .executes(context -> {
                            final String name = StringArgumentType.getString(context, "name");

                            if (DeleteArrayListEntry(name)){
                                context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " wurde gelöscht."), false);
                            }
                            else {
                                context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " wurde nicht gefunden."), false);
                            }
                            return 1;

                        }
        )))));
    }





    public static int ReturnAllCoordinates(CommandContext<ServerCommandSource> context){
        if(ArrayListCoords.stream().count() == 0){
            context.getSource().sendFeedback(() -> Text.literal("Es wurden noch keine Koordinaten gespeichert!"), false);
        }else {
            String header = String.format("§6| %-15s| %6s | %6s | %6s |", "§6Name", "§6X", "§6Y", "§6Z");
            context.getSource().sendFeedback(() -> Text.literal(header), false);

            for (Coordinates c : ArrayListCoords) {
                context.getSource().sendFeedback(() -> Text.literal(c.toString()), false);
            }

        }

        return 1;
    }

    public static int AddCustomCoordinatesToArrayList(CommandContext<ServerCommandSource> context){
        boolean ifexist = false;

        final String name = StringArgumentType.getString(context, "name");
        final int posx = IntegerArgumentType.getInteger(context, "posx");
        final int posy = IntegerArgumentType.getInteger(context, "posy");
        final int posz = IntegerArgumentType.getInteger(context, "posz");

        for (Coordinates arrayListCoord : ArrayListCoords) {
            if (arrayListCoord.getName() != null && arrayListCoord.getName().equals(name)) {
                ifexist = true;
                break;
            }

        }
        if(ifexist) {
            context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " konnte nicht hinzugefügt werden, da bereits ein Punkt mit dem gleichen Namen vorhanden ist."), false);
        } else {
            AddToCoordinates(name, posx, posy, posz);
            AddToJSON(name, posx, posy, posz);
            context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " wurde Hinzugefügt"), false);
        }



        return 1;
    }

    public static int AddCoordinatesOfPlayerToArrayList(@NotNull CommandContext<ServerCommandSource> context){
        boolean ifexist = false;
        ServerPlayerEntity player = context.getSource().getPlayer();
        BlockPos pos = player.getBlockPos();

        int PlayerX = pos.getX();
        int PlayerY = pos.getY();
        int PlayerZ = pos.getZ();

        final String name = StringArgumentType.getString(context, "name");

        for (Coordinates arrayListCoord : ArrayListCoords) {
            if (arrayListCoord.getName() != null && arrayListCoord.getName().equals(name)) {
                ifexist = true;
                break;
            }

        }
        if(ifexist) {
            context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " konnte nicht hinzugefügt werden, da bereits ein Punkt mit dem gleichen Namen vorhanden ist."), false);
        } else {
            AddToCoordinates(name, PlayerX, PlayerY, PlayerZ);
            AddToJSON(name, PlayerX, PlayerY, PlayerZ);
            context.getSource().sendFeedback(() -> Text.literal("Punkt " + name + " wurde Hinzugefügt"), false);
        }

        return 1;
    }

    public static void AddToCoordinates(String name, int Xcoord, int Ycoord, int Zcoord){

        ArrayListCoords.add(new Coordinates(name, Xcoord, Ycoord, Zcoord));

    }

    public static boolean DeleteArrayListEntry(String filterArg){

        boolean ifdel = false;
        File file;
        for (int i = 0; i < ArrayListCoords.size(); i++) {
            if (ArrayListCoords.get(i).getName() != null && ArrayListCoords.get(i).getName().equals(filterArg)) {
                ArrayListCoords.remove(ArrayListCoords.get(i));

                file = new File("./waypoints/" + filterArg + ".json");
                file.delete();

                ifdel = true;
                break;
            }

        }
        return ifdel;

        }

    public static void AddToJSON(String name, int x, int y, int z){

        for (Coordinates arrayListCoord : ArrayListCoords) {
            JSONObject jo = new JSONObject();
            jo.put("name", name);
            jo.put("x", x);
            jo.put("y", y);
            jo.put("z", z);

            try {
                FileWriter file = new FileWriter( "./waypoints/" + name + ".json");
                file.write(jo.toJSONString());
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            }

        }

    public static void ImportJsonToArrayList(){
        String directoryPath = "./waypoints/";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if(files != null){
            for(File file : files){
                try {
                    Object o = new JSONParser().parse(new FileReader("./waypoints/" + file.getName()));
                    JSONObject j = (JSONObject) o;
                    String name = (String) j.get("name");
                    int x = (int) j.get("x");
                    int y = (int) j.get("y");
                    int z = (int) j.get("z");

                    AddToCoordinates(name, x, y, z);

                } //catch phrasen weil parse und FileReader sonst nicht gehen
                catch(FileNotFoundException | ParseException e){
                    throw new RuntimeException(e);

                }

            }

        }
    }
}

