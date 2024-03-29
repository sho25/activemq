begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jms
operator|.
name|pool
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|TransportConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|RegionBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
import|;
end_import

begin_comment
comment|/**  * Test of lingering temporary destinations on pooled connections when the  * underlying connections are reused. Also tests that closing one  * PooledConnection does not delete the temporary destinations of another  * PooledConnection that uses the same underlying ConnectionPool.  *  * jira: AMQ-3457  */
end_comment

begin_class
specifier|public
class|class
name|PooledConnectionTempDestCleanupTest
extends|extends
name|JmsPoolTestSupport
block|{
annotation|@
name|Rule
specifier|public
name|TestName
name|testName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
specifier|protected
name|ActiveMQConnectionFactory
name|directConnFact
decl_stmt|;
specifier|protected
name|Connection
name|directConn1
decl_stmt|;
specifier|protected
name|Connection
name|directConn2
decl_stmt|;
specifier|protected
name|PooledConnectionFactory
name|pooledConnFact
decl_stmt|;
specifier|protected
name|Connection
name|pooledConn1
decl_stmt|;
specifier|protected
name|Connection
name|pooledConn2
decl_stmt|;
specifier|protected
name|TemporaryQueue
name|tempDest
decl_stmt|;
specifier|protected
name|TemporaryQueue
name|otherTempDest
decl_stmt|;
comment|/**      * Prepare to run a test case: create, configure, and start the embedded      * broker, as well as creating the client connections to the broker.      */
annotation|@
name|Override
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|configureBroker
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
comment|// Create the ActiveMQConnectionFactory and the PooledConnectionFactory.
name|directConnFact
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|getBrokerConnectionURI
argument_list|()
argument_list|)
expr_stmt|;
name|pooledConnFact
operator|=
operator|new
name|PooledConnectionFactory
argument_list|()
expr_stmt|;
name|pooledConnFact
operator|.
name|setConnectionFactory
argument_list|(
name|directConnFact
argument_list|)
expr_stmt|;
comment|// Prepare the connections
name|directConn1
operator|=
name|directConnFact
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|directConn1
operator|.
name|start
argument_list|()
expr_stmt|;
name|directConn2
operator|=
name|directConnFact
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|directConn2
operator|.
name|start
argument_list|()
expr_stmt|;
name|pooledConn1
operator|=
name|pooledConnFact
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|pooledConn1
operator|.
name|start
argument_list|()
expr_stmt|;
name|pooledConn2
operator|=
name|pooledConnFact
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|pooledConn2
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
try|try
block|{
name|pooledConn1
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jms_exc
parameter_list|)
block|{         }
try|try
block|{
name|pooledConn2
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jms_exc
parameter_list|)
block|{         }
try|try
block|{
name|directConn1
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jms_exc
parameter_list|)
block|{         }
try|try
block|{
name|directConn2
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jms_exc
parameter_list|)
block|{         }
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|setBrokerName
argument_list|(
literal|"testbroker1"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setSchedulerSupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TransportConnector
name|connector
init|=
operator|new
name|TransportConnector
argument_list|()
decl_stmt|;
name|connector
operator|.
name|setUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setName
argument_list|(
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
name|connector
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getBrokerConnectionURI
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|brokerService
operator|.
name|getTransportConnectorByName
argument_list|(
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
return|;
block|}
comment|/**      * Test for lingering temporary destinations after closing a      * PooledConnection. Here are the steps:      *      * 1. create a session on the first pooled connection 2. create a session on      * the second pooled connection 3. create a temporary destination on the      * first session 4. confirm the temporary destination exists in the broker      * 5. close the first connection 6. check that the temporary destination no      * longer exists in the broker      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testPooledLingeringTempDests
parameter_list|()
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
name|Session
name|session1
decl_stmt|;
name|Session
name|session2
decl_stmt|;
name|session1
operator|=
name|pooledConn1
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|session2
operator|=
name|pooledConn2
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|tempDest
operator|=
name|session1
operator|.
name|createTemporaryQueue
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"TEST METHOD FAILURE - NEW TEMP DESTINATION DOES NOT EXIST"
argument_list|,
name|destinationExists
argument_list|(
name|tempDest
argument_list|)
argument_list|)
expr_stmt|;
name|pooledConn1
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FAILED: temp dest from closed pooled connection is lingering"
argument_list|,
operator|!
name|destinationExists
argument_list|(
name|tempDest
argument_list|)
argument_list|)
expr_stmt|;
name|session2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test that closing one PooledConnection does not delete the temporary      * destinations of another.      *      * 1. create a session on the first pooled connection 2. create a session on      * the second pooled connection 3. create a temporary destination on the      * first session 4. create a temporary destination on the second session 5.      * confirm both temporary destinations exist in the broker 6. close the      * first connection 7. check that the first temporary destination no longer      * exists in the broker 8. check that the second temporary destination does      * still exist in the broker      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testPooledTempDestsCleanupOverzealous
parameter_list|()
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
name|Session
name|session1
decl_stmt|;
name|Session
name|session2
decl_stmt|;
name|session1
operator|=
name|pooledConn1
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|session2
operator|=
name|pooledConn2
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|tempDest
operator|=
name|session1
operator|.
name|createTemporaryQueue
argument_list|()
expr_stmt|;
name|otherTempDest
operator|=
name|session2
operator|.
name|createTemporaryQueue
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"TEST METHOD FAILURE - NEW TEMP DESTINATION DOES NOT EXIST"
argument_list|,
name|destinationExists
argument_list|(
name|tempDest
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"TEST METHOD FAILURE - NEW TEMP DESTINATION DOES NOT EXIST"
argument_list|,
name|destinationExists
argument_list|(
name|otherTempDest
argument_list|)
argument_list|)
expr_stmt|;
name|pooledConn1
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now confirm the first temporary destination no longer exists and the
comment|// second does.
name|assertTrue
argument_list|(
literal|"FAILED: temp dest from closed pooled connection is lingering"
argument_list|,
operator|!
name|destinationExists
argument_list|(
name|tempDest
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FAILED: second PooledConnectin's temporary destination was incorrectly deleted"
argument_list|,
name|destinationExists
argument_list|(
name|otherTempDest
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * CONTROL CASE      *      * Test for lingering temporary destinations after closing a Connection that      * is NOT pooled. This demonstrates the standard JMS operation and helps to      * validate the test methodology.      *      * 1. create a session on the first direct connection 2. create a session on      * the second direct connection 3. create a temporary destination on the      * first session 4. confirm the destination exists in the broker 5. close      * the first connection 6. check that the destination no longer exists in      * the broker      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testDirectLingeringTempDests
parameter_list|()
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
name|Session
name|session1
decl_stmt|;
name|Session
name|session2
decl_stmt|;
name|session1
operator|=
name|directConn1
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|session2
operator|=
name|directConn2
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|tempDest
operator|=
name|session1
operator|.
name|createTemporaryQueue
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"TEST METHOD FAILURE - NEW TEMP DESTINATION DOES NOT EXIST"
argument_list|,
name|destinationExists
argument_list|(
name|tempDest
argument_list|)
argument_list|)
expr_stmt|;
name|directConn1
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now confirm the temporary destination no longer exists.
name|assertTrue
argument_list|(
literal|"CONTROL TEST FAILURE - TEST METHOD IS SUSPECT"
argument_list|,
operator|(
operator|!
name|destinationExists
argument_list|(
name|tempDest
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|session2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|boolean
name|destinationExists
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|RegionBroker
name|rb
init|=
operator|(
name|RegionBroker
operator|)
name|brokerService
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|RegionBroker
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|rb
operator|.
name|getTopicRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|dest
argument_list|)
operator|||
name|rb
operator|.
name|getQueueRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|dest
argument_list|)
operator|||
name|rb
operator|.
name|getTempTopicRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|dest
argument_list|)
operator|||
name|rb
operator|.
name|getTempQueueRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|dest
argument_list|)
return|;
block|}
block|}
end_class

end_unit

