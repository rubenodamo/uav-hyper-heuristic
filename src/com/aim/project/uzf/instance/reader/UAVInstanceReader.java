package com.aim.project.uzf.instance.reader;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.aim.project.uzf.instance.Location;
import com.aim.project.uzf.instance.UZFInstance;
import com.aim.project.uzf.interfaces.UZFInstanceInterface;
import com.aim.project.uzf.interfaces.UAVInstanceReaderInterface;

/**
 * @author Warren G Jackson
 * @since 1.0.0 (22/03/2024)
 */
public class UAVInstanceReader implements UAVInstanceReaderInterface {

	@Override
	public UZFInstanceInterface readUZFInstance(Path path, Random random) {

		try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
			String line;

			String name = "";
			String comment = "";

			List<Location> enclosures = new ArrayList<>();
			Location preparationArea = null;

			while ((line = reader.readLine()) != null) {
				if (line.startsWith("NAME")) {
					name = line.substring(line.indexOf(":") + 1).trim();
				} else if (line.startsWith("COMMENT")) {
					comment = line.substring(line.indexOf(":") + 1).trim();
				} else if (line.startsWith("PREPARATION_AREA")) {
					String[] coordinates = reader.readLine().trim().split("\\s+");
					int preparationAreaX = Integer.parseInt(coordinates[0]);
					int preparationAreaY = Integer.parseInt(coordinates[1]);
					preparationArea = new Location(preparationAreaX, preparationAreaY);
				} else if (line.startsWith("ENCLOSURE_LOCATIONS")) {
					while (!(line = reader.readLine().trim()).equals("EOF")) {
						String[] coordinates = line.split("\\s+");
						int x = Integer.parseInt(coordinates[0]);
						int y = Integer.parseInt(coordinates[1]);
						enclosures.add(new Location(x, y));
					}
				}
			}

			return new UZFInstance(enclosures.size(), enclosures.toArray(new Location[0]), preparationArea, random);
		} catch (IOException e) {
			e.printStackTrace();
		}


		return null;
	}
}
