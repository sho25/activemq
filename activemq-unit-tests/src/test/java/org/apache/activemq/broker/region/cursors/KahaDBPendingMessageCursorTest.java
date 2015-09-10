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
name|broker
operator|.
name|region
operator|.
name|cursors
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|DeliveryMode
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
name|region
operator|.
name|Topic
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
name|util
operator|.
name|SubscriptionKey
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
name|io
operator|.
name|FileUtils
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
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This test checks that pending message metrics work properly with KahaDB  *  * AMQ-5923  *  */
end_comment

begin_class
specifier|public
class|class
name|KahaDBPendingMessageCursorTest
extends|extends
name|AbstractPendingMessageCursorTest
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KahaDBPendingMessageCursorTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|File
name|dataFileDir
init|=
operator|new
name|File
argument_list|(
literal|"target/test-amq-5923/pending-datadb"
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUpBroker
parameter_list|(
name|boolean
name|clearDataDir
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|clearDataDir
operator|&&
name|dataFileDir
operator|.
name|exists
argument_list|()
condition|)
name|FileUtils
operator|.
name|cleanDirectory
argument_list|(
name|dataFileDir
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUpBroker
argument_list|(
name|clearDataDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|initPersistence
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|IOException
block|{
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDataDirectoryFile
argument_list|(
name|dataFileDir
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test that the the counter restores size and works after restart and more      * messages are published      *      * @throws Exception      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
specifier|public
name|void
name|testDurableMessageSizeAfterRestartAndPublish
parameter_list|()
throws|throws
name|Exception
block|{
name|AtomicLong
name|publishedMessageSize
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerConnectURI
argument_list|)
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"clientId"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Topic
name|topic
init|=
name|publishTestMessagesDurable
argument_list|(
name|connection
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"sub1"
block|}
argument_list|,
literal|200
argument_list|,
name|publishedMessageSize
argument_list|,
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
decl_stmt|;
name|SubscriptionKey
name|subKey
init|=
operator|new
name|SubscriptionKey
argument_list|(
literal|"clientId"
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
comment|// verify the count and size
name|verifyPendingStats
argument_list|(
name|topic
argument_list|,
name|subKey
argument_list|,
literal|200
argument_list|,
name|publishedMessageSize
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|verifyStoreStats
argument_list|(
name|topic
argument_list|,
literal|200
argument_list|,
name|publishedMessageSize
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// stop, restart broker and publish more messages
name|stopBroker
argument_list|()
expr_stmt|;
name|this
operator|.
name|setUpBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerConnectURI
argument_list|)
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"clientId"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|topic
operator|=
name|publishTestMessagesDurable
argument_list|(
name|connection
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"sub1"
block|}
argument_list|,
literal|200
argument_list|,
name|publishedMessageSize
argument_list|,
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
comment|// verify the count and size
name|verifyPendingStats
argument_list|(
name|topic
argument_list|,
name|subKey
argument_list|,
literal|400
argument_list|,
name|publishedMessageSize
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|verifyStoreStats
argument_list|(
name|topic
argument_list|,
literal|400
argument_list|,
name|publishedMessageSize
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test that the the counter restores size and works after restart and more      * messages are published      *      * @throws Exception      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
specifier|public
name|void
name|testNonPersistentDurableMessageSize
parameter_list|()
throws|throws
name|Exception
block|{
name|AtomicLong
name|publishedMessageSize
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerConnectURI
argument_list|)
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"clientId"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Topic
name|topic
init|=
name|publishTestMessagesDurable
argument_list|(
name|connection
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"sub1"
block|}
argument_list|,
literal|200
argument_list|,
name|publishedMessageSize
argument_list|,
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
decl_stmt|;
name|SubscriptionKey
name|subKey
init|=
operator|new
name|SubscriptionKey
argument_list|(
literal|"clientId"
argument_list|,
literal|"sub1"
argument_list|)
decl_stmt|;
comment|// verify the count and size
name|verifyPendingStats
argument_list|(
name|topic
argument_list|,
name|subKey
argument_list|,
literal|200
argument_list|,
name|publishedMessageSize
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|verifyStoreStats
argument_list|(
name|topic
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
