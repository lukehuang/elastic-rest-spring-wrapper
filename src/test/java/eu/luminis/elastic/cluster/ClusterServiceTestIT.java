package eu.luminis.elastic.cluster;

import eu.luminis.elastic.RestClientConfig;
import eu.luminis.elastic.TestConfig;
import eu.luminis.elastic.cluster.response.ClusterHealth;
import eu.luminis.elastic.index.IndexService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RestClientConfig.class, TestConfig.class})
public class ClusterServiceTestIT {

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private IndexService indexService;

    @Test
    public void checkClusterHealth() throws Exception {
        if (!indexService.indexExist("cluster_test")) {
            indexService.createIndex("cluster_test", "{}");
        }

        ClusterHealth clusterHealth = clusterService.checkClusterHealth();
        assertEquals("test",clusterHealth.getClusterName());
        assertEquals(1,clusterHealth.getNumberOfNodes());
        assertEquals("yellow", clusterHealth.getStatus());
        assertTrue(clusterHealth.getActivePrimaryShards()>= 5);
        assertTrue(clusterHealth.getUnassignedShards() >= 5);
        assertTrue(clusterHealth.getActiveShards() >= 5);
        assertEquals(0,clusterHealth.getRelocatingShards());
        assertEquals(0,clusterHealth.getInitializingShards());
        assertEquals(0,clusterHealth.getDelayedUnassignedShards());
        assertFalse(clusterHealth.getTimedOut());
        assertEquals(1, clusterHealth.getNumberOfDataNodes());
        assertEquals(1, clusterHealth.getNumberOfDataNodes());
        assertEquals(0,clusterHealth.getNumberOfPendingTasks());
        assertEquals(0,clusterHealth.getNumberOfInFlightFetch());
        assertEquals(0,clusterHealth.getTaskMaxWaitingInQueueMillis());
        assertNotNull(clusterHealth.toString());
    }

}