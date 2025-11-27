package org.example.flowinstall;

import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.DeviceId;
import org.onosproject.net.flow.DefaultFlowRule;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.PortNumber;
import org.onlab.packet.IpAddress;
import org.onlab.packet.IpPrefix;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ONOS Application Component for installing OpenFlow 1.3 flows.
 * This application demonstrates how to install flow rules compatible with OpenFlow 1.3.
 */
@Component(immediate = true)
public class FlowInstallApp {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowRuleService flowRuleService;

    private ApplicationId appId;

    @Activate
    protected void activate() {
        appId = coreService.registerApplication("org.example.flowinstall");
        log.info("FlowInstallApp Started with Application ID: {}", appId.id());
        log.info("This application is compatible with OpenFlow 1.3");
    }

    @Deactivate
    protected void deactivate() {
        log.info("FlowInstallApp Stopped");
    }

    /**
     * Install a flow rule on a device.
     * This method creates and installs a flow rule compatible with OpenFlow 1.3.
     *
     * @param deviceId The device ID where the flow will be installed
     * @param inPort Input port number
     * @param outPort Output port number
     * @param priority Flow priority
     * @param timeout Flow timeout in seconds (0 for permanent)
     */
    public void installFlow(DeviceId deviceId, PortNumber inPort, PortNumber outPort, 
                           int priority, int timeout) {
        
        // Create traffic selector (match fields) - OpenFlow 1.3 compatible
        TrafficSelector.Builder selectorBuilder = DefaultTrafficSelector.builder();
        selectorBuilder.matchInPort(inPort);
        
        // Create traffic treatment (actions) - OpenFlow 1.3 compatible
        TrafficTreatment.Builder treatmentBuilder = DefaultTrafficTreatment.builder();
        treatmentBuilder.setOutput(outPort);
        
        TrafficSelector selector = selectorBuilder.build();
        TrafficTreatment treatment = treatmentBuilder.build();
        
        // Create flow rule
        FlowRule flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .withSelector(selector)
                .withTreatment(treatment)
                .withPriority(priority)
                .fromApp(appId)
                .makePermanent() // Permanent flow (timeout = 0)
                .build();
        
        // Install the flow rule
        flowRuleService.applyFlowRules(flowRule);
        log.info("Flow rule installed on device {}: inPort={}, outPort={}, priority={}", 
                deviceId, inPort, outPort, priority);
    }

    /**
     * Install a flow rule with IP matching (OpenFlow 1.3 compatible).
     *
     * @param deviceId The device ID
     * @param srcIp Source IP address (e.g., "10.0.0.1")
     * @param dstIp Destination IP address (e.g., "10.0.0.2")
     * @param outPort Output port
     * @param priority Flow priority
     */
    public void installIpFlow(DeviceId deviceId, String srcIp, String dstIp, 
                              PortNumber outPort, int priority) {
        
        // Create IP prefix from IP addresses (OpenFlow 1.3 compatible)
        IpPrefix srcIpPrefix = IpPrefix.valueOf(IpAddress.valueOf(srcIp), 32);
        IpPrefix dstIpPrefix = IpPrefix.valueOf(IpAddress.valueOf(dstIp), 32);
        
        TrafficSelector selector = DefaultTrafficSelector.builder()
                .matchIPSrc(srcIpPrefix)
                .matchIPDst(dstIpPrefix)
                .build();
        
        TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                .setOutput(outPort)
                .build();
        
        FlowRule flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .withSelector(selector)
                .withTreatment(treatment)
                .withPriority(priority)
                .fromApp(appId)
                .makePermanent()
                .build();
        
        flowRuleService.applyFlowRules(flowRule);
        log.info("IP Flow rule installed on device {}: srcIp={}, dstIp={}, outPort={}", 
                deviceId, srcIp, dstIp, outPort);
    }

    /**
     * Remove all flows installed by this application.
     *
     * @param deviceId The device ID
     */
    public void removeFlows(DeviceId deviceId) {
        flowRuleService.removeFlowRulesById(appId);
        log.info("All flows removed from device {} for application {}", deviceId, appId);
    }

    /**
     * Get all flows installed by this application on a device.
     *
     * @param deviceId The device ID
     * @return List of flow rules
     */
    public List<FlowRule> getFlows(DeviceId deviceId) {
        List<FlowRule> appFlows = new ArrayList<>();
        Iterable<FlowEntry> allFlows = flowRuleService.getFlowEntries(deviceId);
        short appIdValue = appId.id();
        for (FlowEntry flowEntry : allFlows) {
            if (flowEntry.appId() == appIdValue) {
                appFlows.add(flowEntry);
            }
        }
        return appFlows;
    }
}

