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
name|jmx
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
name|assertFalse
import|;
end_import

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
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerPlugin
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
name|filter
operator|.
name|DestinationMapEntry
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
name|security
operator|.
name|AuthorizationEntry
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
name|security
operator|.
name|AuthorizationMap
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
name|security
operator|.
name|AuthorizationPlugin
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
name|security
operator|.
name|DefaultAuthorizationMap
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
name|security
operator|.
name|JaasAuthenticationPlugin
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
name|Test
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
comment|/**  * Makes sure a durable subscriber can be added and deleted from the  * brokerServer.getAdminView() when JAAS authentication and authorization are  * setup  */
end_comment

begin_class
specifier|public
class|class
name|JMXRemoveDurableSubscriberTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JMXRemoveDurableSubscriberTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|JaasAuthenticationPlugin
name|jaasAuthenticationPlugin
init|=
operator|new
name|JaasAuthenticationPlugin
argument_list|()
decl_stmt|;
name|jaasAuthenticationPlugin
operator|.
name|setDiscoverLoginConfig
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|BrokerPlugin
index|[]
name|brokerPlugins
init|=
operator|new
name|BrokerPlugin
index|[
literal|2
index|]
decl_stmt|;
name|brokerPlugins
index|[
literal|0
index|]
operator|=
name|jaasAuthenticationPlugin
expr_stmt|;
name|AuthorizationPlugin
name|authorizationPlugin
init|=
operator|new
name|AuthorizationPlugin
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DestinationMapEntry
argument_list|>
name|destinationMapEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|DestinationMapEntry
argument_list|>
argument_list|()
decl_stmt|;
comment|// Add Authorization Entries.
name|AuthorizationEntry
name|authEntry1
init|=
operator|new
name|AuthorizationEntry
argument_list|()
decl_stmt|;
name|authEntry1
operator|.
name|setRead
argument_list|(
literal|"manager,viewer,Operator,Maintainer,Deployer,Auditor,Administrator,SuperUser, admin"
argument_list|)
expr_stmt|;
name|authEntry1
operator|.
name|setWrite
argument_list|(
literal|"manager,Operator,Maintainer,Deployer,Auditor,Administrator,SuperUser,admin"
argument_list|)
expr_stmt|;
name|authEntry1
operator|.
name|setAdmin
argument_list|(
literal|"manager,Operator,Maintainer,Deployer,Auditor,Administrator,SuperUser,admin"
argument_list|)
expr_stmt|;
name|authEntry1
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|AuthorizationEntry
name|authEntry2
init|=
operator|new
name|AuthorizationEntry
argument_list|()
decl_stmt|;
name|authEntry2
operator|.
name|setRead
argument_list|(
literal|"manager,viewer,Operator,Maintainer,Deployer,Auditor,Administrator,SuperUser, admin"
argument_list|)
expr_stmt|;
name|authEntry2
operator|.
name|setWrite
argument_list|(
literal|"manager,Operator,Maintainer,Deployer,Auditor,Administrator,SuperUser,admin"
argument_list|)
expr_stmt|;
name|authEntry2
operator|.
name|setAdmin
argument_list|(
literal|"manager,Operator,Maintainer,Deployer,Auditor,Administrator,SuperUser,admin"
argument_list|)
expr_stmt|;
name|authEntry2
operator|.
name|setTopic
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|AuthorizationEntry
name|authEntry3
init|=
operator|new
name|AuthorizationEntry
argument_list|()
decl_stmt|;
name|authEntry3
operator|.
name|setRead
argument_list|(
literal|"manager,viewer,Operator,Maintainer,Deployer,Auditor,Administrator,SuperUser, admin"
argument_list|)
expr_stmt|;
name|authEntry3
operator|.
name|setWrite
argument_list|(
literal|"manager,Operator,Maintainer,Deployer,Auditor,Administrator,SuperUser,admin"
argument_list|)
expr_stmt|;
name|authEntry3
operator|.
name|setAdmin
argument_list|(
literal|"manager,Operator,Maintainer,Deployer,Auditor,Administrator,SuperUser,admin"
argument_list|)
expr_stmt|;
name|authEntry3
operator|.
name|setTopic
argument_list|(
literal|"ActiveMQ.Advisory.>"
argument_list|)
expr_stmt|;
name|destinationMapEntries
operator|.
name|add
argument_list|(
name|authEntry1
argument_list|)
expr_stmt|;
name|destinationMapEntries
operator|.
name|add
argument_list|(
name|authEntry2
argument_list|)
expr_stmt|;
name|destinationMapEntries
operator|.
name|add
argument_list|(
name|authEntry3
argument_list|)
expr_stmt|;
name|AuthorizationMap
name|authorizationMap
init|=
operator|new
name|DefaultAuthorizationMap
argument_list|(
name|destinationMapEntries
argument_list|)
decl_stmt|;
name|authorizationPlugin
operator|.
name|setMap
argument_list|(
name|authorizationMap
argument_list|)
expr_stmt|;
name|brokerPlugins
index|[
literal|1
index|]
operator|=
name|authorizationPlugin
expr_stmt|;
name|brokerService
operator|.
name|setPlugins
argument_list|(
name|brokerPlugins
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setBrokerName
argument_list|(
literal|"ActiveMQBroker"
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
name|setUseVirtualTopics
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{             }
block|}
block|}
comment|/**      * Creates a durable subscription via the AdminView      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testCreateDurableSubsciber
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clientId
init|=
literal|"10"
decl_stmt|;
comment|// Add a topic called test topic
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|addTopic
argument_list|(
literal|"testTopic"
argument_list|)
expr_stmt|;
name|boolean
name|createSubscriberSecurityException
init|=
literal|false
decl_stmt|;
name|String
name|subscriberName
init|=
literal|"testSubscriber"
decl_stmt|;
comment|// Create a durable subscriber with the name testSubscriber
try|try
block|{
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|createDurableSubscriber
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|,
literal|"testTopic"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully created durable subscriber "
operator|+
name|subscriberName
operator|+
literal|" via AdminView"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|SecurityException
name|se1
parameter_list|)
block|{
if|if
condition|(
name|se1
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"User is not authenticated."
argument_list|)
condition|)
block|{
name|createSubscriberSecurityException
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertFalse
argument_list|(
name|createSubscriberSecurityException
argument_list|)
expr_stmt|;
comment|// Delete the durable subscriber that was created earlier.
name|boolean
name|destroySubscriberSecurityException
init|=
literal|false
decl_stmt|;
try|try
block|{
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|destroyDurableSubscriber
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully destroyed durable subscriber "
operator|+
name|subscriberName
operator|+
literal|" via AdminView"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|SecurityException
name|se2
parameter_list|)
block|{
if|if
condition|(
name|se2
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"User is not authenticated."
argument_list|)
condition|)
block|{
name|destroySubscriberSecurityException
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertFalse
argument_list|(
name|destroySubscriberSecurityException
argument_list|)
expr_stmt|;
comment|// Just to make sure the subscriber was actually deleted, try deleting
comment|// the subscriber again
comment|// and that should throw an exception
name|boolean
name|subscriberAlreadyDeleted
init|=
literal|false
decl_stmt|;
try|try
block|{
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|destroyDurableSubscriber
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully destroyed durable subscriber "
operator|+
name|subscriberName
operator|+
literal|" via AdminView"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|javax
operator|.
name|jms
operator|.
name|InvalidDestinationException
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"No durable subscription exists for clientID: 10 and subscriptionName: testSubscriber"
argument_list|)
condition|)
block|{
name|subscriberAlreadyDeleted
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|subscriberAlreadyDeleted
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

