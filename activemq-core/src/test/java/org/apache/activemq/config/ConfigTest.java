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
name|config
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|policy
operator|.
name|FixedSizedSubscriptionRecoveryPolicy
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
name|policy
operator|.
name|LastImageSubscriptionRecoveryPolicy
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
name|policy
operator|.
name|NoSubscriptionRecoveryPolicy
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
name|policy
operator|.
name|RoundRobinDispatchPolicy
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
name|policy
operator|.
name|SimpleDispatchPolicy
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
name|policy
operator|.
name|StrictOrderDispatchPolicy
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
name|policy
operator|.
name|SubscriptionRecoveryPolicy
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
name|policy
operator|.
name|TimedSubscriptionRecoveryPolicy
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
name|command
operator|.
name|ActiveMQTopic
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
name|memory
operator|.
name|UsageManager
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
name|store
operator|.
name|PersistenceAdapter
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
name|store
operator|.
name|jdbc
operator|.
name|JDBCPersistenceAdapter
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
name|store
operator|.
name|journal
operator|.
name|JournalPersistenceAdapter
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
name|store
operator|.
name|memory
operator|.
name|MemoryPersistenceAdapter
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
name|transport
operator|.
name|tcp
operator|.
name|TcpTransportServer
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
name|wireformat
operator|.
name|ObjectStreamWireFormat
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
name|xbean
operator|.
name|BrokerFactoryBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|derby
operator|.
name|jdbc
operator|.
name|EmbeddedDataSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|FileSystemResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|Resource
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ConfigTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|JOURNAL_ROOT
init|=
literal|"target/test-data/"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|DERBY_ROOT
init|=
literal|"target/test-data/"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|CONF_ROOT
init|=
literal|"src/test/resources/org/apache/activemq/config/sample-conf/"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ConfigTest
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
literal|"src/test/resources/client.keystore"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStoreType"
argument_list|,
literal|"jks"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|,
literal|"src/test/resources/server.keystore"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStoreType"
argument_list|,
literal|"jks"
argument_list|)
expr_stmt|;
block|}
comment|/*      * IMPORTANT NOTE: Assertions checking for the existence of the derby      * directory will fail if the first derby directory is not created under      * target/test-data/. The test in unable to change the derby root directory      * for succeeding creation. It uses the first created directory as the root.      */
comment|/*      * This tests creating a journal persistence adapter using the persistence      * adapter factory bean      */
specifier|public
name|void
name|testJournaledJDBCConfig
parameter_list|()
throws|throws
name|Exception
block|{
comment|// System.out.print("Checking journaled JDBC persistence adapter
comment|// configuration... ");
name|File
name|journalFile
init|=
operator|new
name|File
argument_list|(
name|JOURNAL_ROOT
operator|+
literal|"testJournaledJDBCConfig/journal"
argument_list|)
decl_stmt|;
name|recursiveDelete
argument_list|(
name|journalFile
argument_list|)
expr_stmt|;
name|File
name|derbyFile
init|=
operator|new
name|File
argument_list|(
name|DERBY_ROOT
operator|+
literal|"testJournaledJDBCConfig/derbydb"
argument_list|)
decl_stmt|;
comment|// Default
comment|// derby
comment|// name
name|recursiveDelete
argument_list|(
name|derbyFile
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
operator|new
name|FileSystemResource
argument_list|(
name|CONF_ROOT
operator|+
literal|"journaledjdbc-example.xml"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"Broker Config Error (brokerName)"
argument_list|,
literal|"brokerJournaledJDBCConfigTest"
argument_list|,
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|PersistenceAdapter
name|adapter
init|=
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a journal persistence adapter"
argument_list|,
name|adapter
operator|instanceof
name|JournalPersistenceAdapter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a derby directory at "
operator|+
name|derbyFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|derbyFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a journal directory at "
operator|+
name|journalFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|journalFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check persistence factory configurations
comment|// System.out.print("Checking persistence adapter factory
comment|// settings... ");
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*      * This tests creating a jdbc persistence adapter using xbeans-spring      */
specifier|public
name|void
name|testJdbcConfig
parameter_list|()
throws|throws
name|Exception
block|{
comment|// System.out.print("Checking jdbc persistence adapter configuration...
comment|// ");
name|File
name|derbyFile
init|=
operator|new
name|File
argument_list|(
name|DERBY_ROOT
operator|+
literal|"testJDBCConfig/derbydb"
argument_list|)
decl_stmt|;
comment|// Default
comment|// derby
comment|// name
name|recursiveDelete
argument_list|(
name|derbyFile
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
operator|new
name|FileSystemResource
argument_list|(
name|CONF_ROOT
operator|+
literal|"jdbc-example.xml"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"Broker Config Error (brokerName)"
argument_list|,
literal|"brokerJdbcConfigTest"
argument_list|,
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|PersistenceAdapter
name|adapter
init|=
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a jdbc persistence adapter"
argument_list|,
name|adapter
operator|instanceof
name|JDBCPersistenceAdapter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"JDBC Adapter Config Error (cleanupPeriod)"
argument_list|,
literal|60000
argument_list|,
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|adapter
operator|)
operator|.
name|getCleanupPeriod
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created an EmbeddedDataSource"
argument_list|,
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|adapter
operator|)
operator|.
name|getDataSource
argument_list|()
operator|instanceof
name|EmbeddedDataSource
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a DefaultWireFormat"
argument_list|,
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|adapter
operator|)
operator|.
name|getWireFormat
argument_list|()
operator|instanceof
name|ObjectStreamWireFormat
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*      * This tests configuring the different broker properties using      * xbeans-spring      */
specifier|public
name|void
name|testBrokerConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTopic
name|dest
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|File
name|journalFile
init|=
operator|new
name|File
argument_list|(
name|JOURNAL_ROOT
argument_list|)
decl_stmt|;
name|recursiveDelete
argument_list|(
name|journalFile
argument_list|)
expr_stmt|;
comment|// Create broker from resource
comment|// System.out.print("Creating broker... ");
name|broker
operator|=
name|createBroker
argument_list|(
literal|"org/apache/activemq/config/example.xml"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Check broker configuration
comment|// System.out.print("Checking broker configurations... ");
name|assertEquals
argument_list|(
literal|"Broker Config Error (brokerName)"
argument_list|,
literal|"brokerConfigTest"
argument_list|,
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Broker Config Error (populateJMSXUserID)"
argument_list|,
literal|false
argument_list|,
name|broker
operator|.
name|isPopulateJMSXUserID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Broker Config Error (useLoggingForShutdownErrors)"
argument_list|,
literal|true
argument_list|,
name|broker
operator|.
name|isUseLoggingForShutdownErrors
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Broker Config Error (useJmx)"
argument_list|,
literal|true
argument_list|,
name|broker
operator|.
name|isUseJmx
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Broker Config Error (persistent)"
argument_list|,
literal|false
argument_list|,
name|broker
operator|.
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Broker Config Error (useShutdownHook)"
argument_list|,
literal|false
argument_list|,
name|broker
operator|.
name|isUseShutdownHook
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Broker Config Error (deleteAllMessagesOnStartup)"
argument_list|,
literal|true
argument_list|,
name|broker
operator|.
name|isDeleteAllMessagesOnStartup
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
comment|// Check specific vm transport
comment|// System.out.print("Checking vm connector... ");
name|assertEquals
argument_list|(
literal|"Should have a specific VM Connector"
argument_list|,
literal|"vm://javacoola"
argument_list|,
name|broker
operator|.
name|getVmConnectorURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
comment|// Check transport connectors list
comment|// System.out.print("Checking transport connectors... ");
name|List
name|connectors
init|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created at least 3 connectors"
argument_list|,
name|connectors
operator|.
name|size
argument_list|()
operator|>=
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"1st connector should be TcpTransportServer"
argument_list|,
operator|(
operator|(
name|TransportConnector
operator|)
name|connectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getServer
argument_list|()
operator|instanceof
name|TcpTransportServer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"2nd connector should be TcpTransportServer"
argument_list|,
operator|(
operator|(
name|TransportConnector
operator|)
name|connectors
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getServer
argument_list|()
operator|instanceof
name|TcpTransportServer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"3rd connector should be TcpTransportServer"
argument_list|,
operator|(
operator|(
name|TransportConnector
operator|)
name|connectors
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|getServer
argument_list|()
operator|instanceof
name|TcpTransportServer
argument_list|)
expr_stmt|;
comment|// Check network connectors
comment|// System.out.print("Checking network connectors... ");
name|List
name|networkConnectors
init|=
name|broker
operator|.
name|getNetworkConnectors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should have a single network connector"
argument_list|,
literal|1
argument_list|,
name|networkConnectors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
comment|// Check dispatch policy configuration
comment|// System.out.print("Checking dispatch policies... ");
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"Topic.SimpleDispatch"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have a simple dispatch policy for "
operator|+
name|dest
operator|.
name|getTopicName
argument_list|()
argument_list|,
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|dest
argument_list|)
operator|.
name|getDispatchPolicy
argument_list|()
operator|instanceof
name|SimpleDispatchPolicy
argument_list|)
expr_stmt|;
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"Topic.RoundRobinDispatch"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have a round robin dispatch policy for "
operator|+
name|dest
operator|.
name|getTopicName
argument_list|()
argument_list|,
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|dest
argument_list|)
operator|.
name|getDispatchPolicy
argument_list|()
operator|instanceof
name|RoundRobinDispatchPolicy
argument_list|)
expr_stmt|;
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"Topic.StrictOrderDispatch"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have a strict order dispatch policy for "
operator|+
name|dest
operator|.
name|getTopicName
argument_list|()
argument_list|,
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|dest
argument_list|)
operator|.
name|getDispatchPolicy
argument_list|()
operator|instanceof
name|StrictOrderDispatchPolicy
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
comment|// Check subscription policy configuration
comment|// System.out.print("Checking subscription recovery policies... ");
name|SubscriptionRecoveryPolicy
name|subsPolicy
decl_stmt|;
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"Topic.FixedSizedSubs"
argument_list|)
expr_stmt|;
name|subsPolicy
operator|=
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|dest
argument_list|)
operator|.
name|getSubscriptionRecoveryPolicy
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have a fixed sized subscription recovery policy for "
operator|+
name|dest
operator|.
name|getTopicName
argument_list|()
argument_list|,
name|subsPolicy
operator|instanceof
name|FixedSizedSubscriptionRecoveryPolicy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FixedSizedSubsPolicy Config Error (maximumSize)"
argument_list|,
literal|2000000
argument_list|,
operator|(
operator|(
name|FixedSizedSubscriptionRecoveryPolicy
operator|)
name|subsPolicy
operator|)
operator|.
name|getMaximumSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FixedSizedSubsPolicy Config Error (useSharedBuffer)"
argument_list|,
literal|false
argument_list|,
operator|(
operator|(
name|FixedSizedSubscriptionRecoveryPolicy
operator|)
name|subsPolicy
operator|)
operator|.
name|isUseSharedBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"Topic.LastImageSubs"
argument_list|)
expr_stmt|;
name|subsPolicy
operator|=
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|dest
argument_list|)
operator|.
name|getSubscriptionRecoveryPolicy
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have a last image subscription recovery policy for "
operator|+
name|dest
operator|.
name|getTopicName
argument_list|()
argument_list|,
name|subsPolicy
operator|instanceof
name|LastImageSubscriptionRecoveryPolicy
argument_list|)
expr_stmt|;
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"Topic.NoSubs"
argument_list|)
expr_stmt|;
name|subsPolicy
operator|=
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|dest
argument_list|)
operator|.
name|getSubscriptionRecoveryPolicy
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have no subscription recovery policy for "
operator|+
name|dest
operator|.
name|getTopicName
argument_list|()
argument_list|,
name|subsPolicy
operator|instanceof
name|NoSubscriptionRecoveryPolicy
argument_list|)
expr_stmt|;
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"Topic.TimedSubs"
argument_list|)
expr_stmt|;
name|subsPolicy
operator|=
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|dest
argument_list|)
operator|.
name|getSubscriptionRecoveryPolicy
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have a timed subscription recovery policy for "
operator|+
name|dest
operator|.
name|getTopicName
argument_list|()
argument_list|,
name|subsPolicy
operator|instanceof
name|TimedSubscriptionRecoveryPolicy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TimedSubsPolicy Config Error (recoverDuration)"
argument_list|,
literal|25000
argument_list|,
operator|(
operator|(
name|TimedSubscriptionRecoveryPolicy
operator|)
name|subsPolicy
operator|)
operator|.
name|getRecoverDuration
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
comment|// Check usage manager
comment|// System.out.print("Checking memory manager configurations... ");
name|UsageManager
name|memMgr
init|=
name|broker
operator|.
name|getMemoryManager
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have a memory manager"
argument_list|,
name|memMgr
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"UsageManager Config Error (limit)"
argument_list|,
literal|200000
argument_list|,
name|memMgr
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"UsageManager Config Error (percentUsageMinDelta)"
argument_list|,
literal|20
argument_list|,
name|memMgr
operator|.
name|getPercentUsageMinDelta
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*      * This tests creating a journal persistence adapter using xbeans-spring      */
specifier|public
name|void
name|testJournalConfig
parameter_list|()
throws|throws
name|Exception
block|{
comment|// System.out.print("Checking journal persistence adapter
comment|// configuration... ");
name|File
name|journalFile
init|=
operator|new
name|File
argument_list|(
name|JOURNAL_ROOT
operator|+
literal|"testJournalConfig/journal"
argument_list|)
decl_stmt|;
name|recursiveDelete
argument_list|(
name|journalFile
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
operator|new
name|FileSystemResource
argument_list|(
name|CONF_ROOT
operator|+
literal|"journal-example.xml"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"Broker Config Error (brokerName)"
argument_list|,
literal|"brokerJournalConfigTest"
argument_list|,
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|PersistenceAdapter
name|adapter
init|=
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a journal persistence adapter"
argument_list|,
name|adapter
operator|instanceof
name|JournalPersistenceAdapter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a journal directory at "
operator|+
name|journalFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|journalFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*      * This tests creating a memory persistence adapter using xbeans-spring      */
specifier|public
name|void
name|testMemoryConfig
parameter_list|()
throws|throws
name|Exception
block|{
comment|// System.out.print("Checking memory persistence adapter
comment|// configuration... ");
name|File
name|journalFile
init|=
operator|new
name|File
argument_list|(
name|JOURNAL_ROOT
operator|+
literal|"testMemoryConfig"
argument_list|)
decl_stmt|;
name|recursiveDelete
argument_list|(
name|journalFile
argument_list|)
expr_stmt|;
name|File
name|derbyFile
init|=
operator|new
name|File
argument_list|(
name|DERBY_ROOT
operator|+
literal|"testMemoryConfig"
argument_list|)
decl_stmt|;
name|recursiveDelete
argument_list|(
name|derbyFile
argument_list|)
expr_stmt|;
name|BrokerService
name|broker
decl_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
operator|new
name|FileSystemResource
argument_list|(
name|CONF_ROOT
operator|+
literal|"memory-example.xml"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"Broker Config Error (brokerName)"
argument_list|,
literal|"brokerMemoryConfigTest"
argument_list|,
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|PersistenceAdapter
name|adapter
init|=
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a memory persistence adapter"
argument_list|,
name|adapter
operator|instanceof
name|MemoryPersistenceAdapter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have not created a derby directory at "
operator|+
name|derbyFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
operator|!
name|derbyFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have not created a journal directory at "
operator|+
name|journalFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
operator|!
name|journalFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testXmlConfigHelper
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
decl_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
operator|new
name|FileSystemResource
argument_list|(
name|CONF_ROOT
operator|+
literal|"memory-example.xml"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"Broker Config Error (brokerName)"
argument_list|,
literal|"brokerMemoryConfigTest"
argument_list|,
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
name|broker
operator|=
name|createBroker
argument_list|(
literal|"org/apache/activemq/config/config.xml"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"Broker Config Error (brokerName)"
argument_list|,
literal|"brokerXmlConfigHelper"
argument_list|,
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*      * TODO: Create additional tests for forwarding bridges      */
specifier|protected
specifier|static
name|void
name|recursiveDelete
parameter_list|(
name|File
name|file
parameter_list|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|file
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|recursiveDelete
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
operator|new
name|ClassPathResource
argument_list|(
name|resource
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|Resource
name|resource
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerFactoryBean
name|factory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|factory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|BrokerService
name|broker
init|=
name|factory
operator|.
name|getBroker
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have a broker!"
argument_list|,
name|broker
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|// Broker is already started by default when using the XML file
comment|// broker.start();
return|return
name|broker
return|;
block|}
block|}
end_class

end_unit

