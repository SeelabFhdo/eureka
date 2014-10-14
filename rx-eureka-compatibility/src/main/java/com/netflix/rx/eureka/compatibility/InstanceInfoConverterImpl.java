package com.netflix.rx.eureka.compatibility;

import com.netflix.appinfo.AmazonInfo;
import com.netflix.rx.eureka.registry.DataCenterInfo;
import com.netflix.rx.eureka.registry.InstanceInfo;
import com.netflix.rx.eureka.registry.datacenter.AwsDataCenterInfo;
import com.netflix.rx.eureka.registry.datacenter.BasicDataCenterInfo;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author David Liu
 */
public class InstanceInfoConverterImpl implements InstanceInfoConverter {

    @Override
    public InstanceInfo fromV1(com.netflix.appinfo.InstanceInfo v1Info) {
        InstanceInfo.Builder builder = new InstanceInfo.Builder()
                .withId(v1Info.getAppName() + "_" + v1Info.getId())  // instanceId is not unique for v1Data
                .withAppGroup(v1Info.getAppGroupName())
                .withApp(v1Info.getAppName())
                .withAsg(v1Info.getASGName())
                .withVipAddress(v1Info.getVIPAddress())
                .withSecureVipAddress(v1Info.getSecureVipAddress())
                .withHostname(v1Info.getHostName())
                .withIp(v1Info.getIPAddr())
                .withPorts(toSet(v1Info.getPort()))
                .withSecurePorts(toSet(v1Info.getSecurePort()))
                .withStatus(fromV1(v1Info.getStatus()))
                .withHomePageUrl(v1Info.getHomePageUrl())
                .withStatusPageUrl(v1Info.getStatusPageUrl())
                .withHealthCheckUrls(new HashSet<>(v1Info.getHealthCheckUrls()))
                .withDataCenterInfo(fromV1(v1Info.getDataCenterInfo()));

        return builder.build();
    }

    @Override
    public InstanceInfo.Status fromV1(com.netflix.appinfo.InstanceInfo.InstanceStatus v1Status) {
        return InstanceInfo.Status.toEnum(v1Status.name());
    }

    @Override
    public DataCenterInfo fromV1(com.netflix.appinfo.DataCenterInfo v1DataCenterInfo) {
        DataCenterInfo.DataCenterInfoBuilder builder;

        if (v1DataCenterInfo instanceof AmazonInfo) {
            AmazonInfo v1Info = (AmazonInfo) v1DataCenterInfo;

            builder = new AwsDataCenterInfo.Builder()
                    .withZone(v1Info.get(AmazonInfo.MetaDataKey.availabilityZone))
                    .withAmiId(v1Info.get(AmazonInfo.MetaDataKey.amiId))
                    .withInstanceId(v1Info.get(AmazonInfo.MetaDataKey.instanceId))
                    .withInstanceType(v1Info.get(AmazonInfo.MetaDataKey.instanceType))
                    .withPrivateIPv4(v1Info.get(AmazonInfo.MetaDataKey.localIpv4))
                    .withPublicIPv4(v1Info.get(AmazonInfo.MetaDataKey.publicIpv4))
                    .withPublicHostName(v1Info.get(AmazonInfo.MetaDataKey.publicHostname));
        } else {
            builder = new BasicDataCenterInfo.Builder()
                    .withName(v1DataCenterInfo.getName().name());
        }
        return builder.build();
    }

    @SafeVarargs
    private static <E> HashSet<E> toSet(E... elements) {
        HashSet<E> set = new HashSet<>();
        Collections.addAll(set, elements);
        return set;
    }
}
