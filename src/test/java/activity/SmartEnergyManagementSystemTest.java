package activity;

import org.junit.Before;
import org.junit.Test;

import activity.SmartEnergyManagementSystem;
import activity.SmartEnergyManagementSystem.EnergyManagementResult;
import activity.SmartEnergyManagementSystem.DeviceSchedule;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertSame;

public class SmartEnergyManagementSystemTest {
    // Ocorre um loop infinito no teste a seguir já que a lógica do máximo de energia diário não considera o caso em que o único dispositivo que é possível reduzir tem prioridade máxima
    // @Test()
    // public void tc1(){
    //     double currentPrice=10.0;
    //     double priceThreshold=9.0;
    //     Map<String, Integer> devicePriorities=new HashMap<>();
    //     devicePriorities.put("Heating",1);
    //     devicePriorities.put("Cooling",2);
    //     devicePriorities.put("Security",1);
    //     LocalDateTime currentTime=LocalDateTime.parse("2024-04-20T01:10:00");
    //     double currentTemperature=25.0;
    //     double[] desiredTemperatureRange={20.0,30.0};
    //     double energyUsageLimit=100.0;
    //     double totalEnergyUsedToday=110.0;
    //     List<DeviceSchedule> scheduledDevices=new ArrayList<DeviceSchedule>();
    //     scheduledDevices.add(new DeviceSchedule("Cooling", currentTime));
    //     SmartEnergyManagementSystem system=new SmartEnergyManagementSystem();
    //     EnergyManagementResult result=system.manageEnergy(currentPrice,priceThreshold,devicePriorities,currentTime,currentTemperature,desiredTemperatureRange,energyUsageLimit,totalEnergyUsedToday,scheduledDevices);
    //     Map<String,Boolean> expectedStatus=new HashMap<>();
    //     expectedStatus.put("Heating",false);
    //     expectedStatus.put("Cooling",true);
    //     expectedStatus.put("Security",false);
    //     boolean expectedEnergySave=true;
    //     boolean expectedTemperatureRegulation=false;
    //     double expectedEnergyUsed=111.0;
    //     assertEquals(result.deviceStatus,expectedStatus);
    //     assertEquals(result.energySavingMode,expectedEnergySave);
    //     assertEquals(result.temperatureRegulationActive,expectedTemperatureRegulation);
    //     assertEquals(result.totalEnergyUsed,expectedEnergyUsed,0);
    // }


    // Verificação não passa por não considerar uso adicional por ativação por agendamento 
    @Test()
    public void tc2(){
        double currentPrice=10.0;
        double priceThreshold=9.0;
        Map<String, Integer> devicePriorities=new HashMap<>();
        devicePriorities.put("Heating",2);
        devicePriorities.put("Cooling",2);
        devicePriorities.put("Security",2);
        LocalDateTime currentTime=LocalDateTime.parse("2024-04-20T01:10:00");
        double currentTemperature=25.0;
        double[] desiredTemperatureRange={20.0,30.0};
        double energyUsageLimit=100.0;
        double totalEnergyUsedToday=110.0;
        List<DeviceSchedule> scheduledDevices=new ArrayList<DeviceSchedule>();
        scheduledDevices.add(new DeviceSchedule("Cooling", currentTime));
        SmartEnergyManagementSystem system=new SmartEnergyManagementSystem();
        EnergyManagementResult result=system.manageEnergy(currentPrice,priceThreshold,devicePriorities,currentTime,currentTemperature,desiredTemperatureRange,energyUsageLimit,totalEnergyUsedToday,scheduledDevices);
        Map<String,Boolean> expectedStatus=new HashMap<>();
        expectedStatus.put("Heating",false);
        expectedStatus.put("Cooling",true);
        expectedStatus.put("Security",false);
        boolean expectedEnergySave=true;
        boolean expectedTemperatureRegulation=false;
        double expectedEnergyUsed=111.0;
        assertEquals(expectedStatus,result.deviceStatus);
        assertEquals(expectedEnergySave,result.energySavingMode);
        assertEquals(expectedTemperatureRegulation,result.temperatureRegulationActive);
        assertEquals(expectedEnergyUsed,result.totalEnergyUsed,0);
    }
    //Verificação não passa porque não desliga a resfriamento(cooling) quando deve esquentar
    @Test
    public void tc3(){
        double currentPrice=9.0;
        double priceThreshold=10.0;
        Map<String, Integer> devicePriorities=new HashMap<>();
        devicePriorities.put("Heating",1);
        devicePriorities.put("Cooling",1);
        devicePriorities.put("Security",2);
        LocalDateTime currentTime=LocalDateTime.parse("2024-04-20T10:00:00");
        double currentTemperature=15.0;
        double[] desiredTemperatureRange={20.0,30.0};
        double energyUsageLimit=100.0;
        double totalEnergyUsedToday=100.0;
        List<DeviceSchedule> scheduledDevices=new ArrayList<DeviceSchedule>();
        scheduledDevices.add(new DeviceSchedule("Cooling", LocalDateTime.parse("2024-04-20T11:00:00")));
        SmartEnergyManagementSystem system=new SmartEnergyManagementSystem();
        EnergyManagementResult result=system.manageEnergy(currentPrice,priceThreshold,devicePriorities,currentTime,currentTemperature,desiredTemperatureRange,energyUsageLimit,totalEnergyUsedToday,scheduledDevices);
        Map<String,Boolean> expectedStatus=new HashMap<>();
        expectedStatus.put("Heating",true);
        expectedStatus.put("Cooling",false);
        expectedStatus.put("Security",false);
        boolean expectedEnergySave=false;
        boolean expectedTemperatureRegulation=true;
        double expectedEnergyUsed=99.0;
        assertEquals(expectedStatus,result.deviceStatus);
        assertEquals(expectedEnergySave,result.energySavingMode);
        assertEquals(expectedTemperatureRegulation,result.temperatureRegulationActive);
        assertEquals(expectedEnergyUsed,result.totalEnergyUsed,0);
    }

    //Verificação não passa porque não desliga o aquecimento(heating) quando deve resfriar
    @Test
    public void tc4(){
        double currentPrice=9.0;
        double priceThreshold=10.0;
        Map<String, Integer> devicePriorities=new HashMap<>();
        devicePriorities.put("Heating",1);
        devicePriorities.put("Cooling",1);
        devicePriorities.put("Security",2);
        LocalDateTime currentTime=LocalDateTime.parse("2024-04-20T10:00:00");
        double currentTemperature=35.0;
        double[] desiredTemperatureRange={20.0,30.0};
        double energyUsageLimit=100.0;
        double totalEnergyUsedToday=100.0;
        List<DeviceSchedule> scheduledDevices=new ArrayList<DeviceSchedule>();
        scheduledDevices.add(new DeviceSchedule("Cooling", LocalDateTime.parse("2024-04-20T11:00:00")));
        SmartEnergyManagementSystem system=new SmartEnergyManagementSystem();
        EnergyManagementResult result=system.manageEnergy(currentPrice,priceThreshold,devicePriorities,currentTime,currentTemperature,desiredTemperatureRange,energyUsageLimit,totalEnergyUsedToday,scheduledDevices);
        Map<String,Boolean> expectedStatus=new HashMap<>();
        expectedStatus.put("Heating",true);
        expectedStatus.put("Cooling",false);
        expectedStatus.put("Security",false);
        boolean expectedEnergySave=false;
        boolean expectedTemperatureRegulation=true;
        double expectedEnergyUsed=99.0;
        assertEquals(expectedStatus,result.deviceStatus);
        assertEquals(expectedEnergySave,result.energySavingMode);
        assertEquals(expectedTemperatureRegulation,result.temperatureRegulationActive);
        assertEquals(expectedEnergyUsed,result.totalEnergyUsed,0);
    }

}
