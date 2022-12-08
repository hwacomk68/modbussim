package com.hwacom.modbussim;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.SimpleProcessImage;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.slave.ModbusSlave;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

@SpringBootApplication
public class ModbussimApplication {

	private static Logger logger = Logger.getLogger(ModbussimApplication.class.getName());

	private static List<DataPoint> dataPoints;
	private static int port;
	private static int unitId;
	private static int updateRate;

	@Value("${modbus.slave.port}")
	public void setPort(int value) {
		this.port = value;
	}
	@Value("${modbus.slave.unitId}")
	public void setUnitId(int value) {
		this.unitId = value;
	}
	@Value("${modbus.update.rate}")
	public void setUpdateRate(int value) {
		this.updateRate = value;
	}

	static Map<String, Integer> dataMap = new HashMap<>();

	static ArrayList<Integer> pointsNeedReset = new ArrayList<>();

	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		SpringApplication.run(ModbussimApplication.class, args);

		loadDataPointConfig();

		ProcessImage pi = makeProcessImage();

		try {
			ModbusSlave slave = ModbusSlaveFactory.createTCPSlave(port, 100);
			slave.addProcessImage(unitId, pi);
			slave.open();

			while (true) {
				try {
					pointsNeedReset.clear();
					for (DataPoint dp:dataPoints) {
						int value = 0;
						int min, max;

						if (dp.isGen()) {
							min = dp.getMin();
							max = dp.getMax();

							if (dp.isSpecify()) {
								for (SpecifyTime st:dp.getSpecifyTimes()) {
									Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"));
									calendarStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(st.getStart().substring(0, 2)));
									calendarStart.set(Calendar.MINUTE, Integer.parseInt(st.getStart().substring(2, 4)));
									calendarStart.set(Calendar.SECOND, 0);

									Calendar calendarEnd = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"));
									calendarEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(st.getEnd().substring(0, 2)));
									calendarEnd.set(Calendar.MINUTE, Integer.parseInt(st.getEnd().substring(2, 4)));
									calendarEnd.set(Calendar.SECOND, 0);

									Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"));

									if (calendarCurrent.after(calendarStart) && calendarCurrent.before(calendarEnd)) {
										min = st.getMin();
										max = st.getMax();
									}
								}
							}

							if (dp.isAcc()) {
								value = dataMap.getOrDefault(Integer.toString(dp.getId()), 0) + getRandomIntInRange(min, max);
								dataMap.put(Integer.toString(dp.getId()), value);
							} else {
								value = getRandomIntInRange(min, max);
							}
							if (dp.isAccTo()) {
								for (int i = 0; i < dp.getAccToId().length; i++) {
									int tot = dataMap.getOrDefault(Integer.toString(dp.getAccToId()[i]), 0) + value;
									dataMap.put(Integer.toString(dp.getAccToId()[i]), tot);
								}
							}
						} else {
							value = dataMap.getOrDefault(Integer.toString(dp.getId()), 0);
						}
						set32BitsMeterValue(slave, unitId, value, dp.getStart(), dp.getEnd());
						System.out.println(dtf.format(LocalDateTime.now()) + " : " + dp.getDesc() + " : " + value);
						if (dp.isResetValue()) {
							pointsNeedReset.add(dp.getId());
						}
					}
					for (Integer id: pointsNeedReset) {
						dataMap.put(Integer.toString(id), 0);
					}
					System.out.println("-");
					Thread.sleep(updateRate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (ModbusException e) {
			e.printStackTrace();
		}
	}

	private static ProcessImage makeProcessImage() {
		SimpleProcessImage spi = null;
		spi = new SimpleProcessImage();
		for (int i = 0; i < 65536; i++) {
			spi.addRegister(new SimpleRegister(0));
		}
		return spi;
	}

	public static int getRandomIntInRange(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}

	public static void set32BitsMeterValue(ModbusSlave slave, int unitId, int value, int addr1, int addr2) {
		if (slave == null)
			return;
		String hex = Integer.toHexString(value);
		if (hex.length()<8) {
			hex = String.format("%8s", hex);
			hex = hex.replace(' ','0');
		}
		String highWordHex = hex.substring(0, 4);
		String lowWordHex = hex.substring(4, 8);
		int highWordDec = Integer.parseInt(highWordHex, 16);
		int lowWordDec = Integer.parseInt(lowWordHex, 16);
		slave.getProcessImage(unitId).getRegister(addr1).setValue(highWordDec);
		slave.getProcessImage(unitId).getRegister(addr2).setValue(lowWordDec);
	}

	private static void loadDataPointConfig() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Resource sourceFile = new ClassPathResource("datapoints.json");
			String file = new String(FileCopyUtils.copyToByteArray(sourceFile.getInputStream()));
			dataPoints = Arrays.asList(mapper.readValue(file, DataPoint[].class));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
