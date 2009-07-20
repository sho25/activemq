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
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|CombinationTestSupport
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_comment
comment|// from https://issues.apache.org/activemq/browse/AMQ-2216
end_comment

begin_class
specifier|public
class|class
name|DestinationRemoveRestartTest
extends|extends
name|CombinationTestSupport
block|{
specifier|private
specifier|final
specifier|static
name|String
name|destinationName
init|=
literal|"TEST"
decl_stmt|;
specifier|public
name|byte
name|destinationType
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestCheckDestinationRemoveActionAfterRestart
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"destinationType"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
block|,
name|Byte
operator|.
name|valueOf
argument_list|(
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCheckDestinationRemoveActionAfterRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|doAddDestination
argument_list|()
expr_stmt|;
name|doRemoveDestination
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|doCheckRemoveActionAfterRestart
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|doAddDestination
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|res
init|=
literal|false
decl_stmt|;
name|ActiveMQDestination
name|amqDestination
init|=
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|destinationName
argument_list|,
name|destinationType
argument_list|)
decl_stmt|;
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|addDestination
argument_list|(
name|broker
operator|.
name|getAdminConnectionContext
argument_list|()
argument_list|,
operator|(
name|ActiveMQDestination
operator|)
name|amqDestination
argument_list|)
expr_stmt|;
specifier|final
name|ActiveMQDestination
index|[]
name|list
init|=
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinations
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ActiveMQDestination
name|element
range|:
name|list
control|)
block|{
specifier|final
name|Destination
name|destination
init|=
name|broker
operator|.
name|getDestination
argument_list|(
name|element
argument_list|)
decl_stmt|;
if|if
condition|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|equals
argument_list|(
name|destinationName
argument_list|)
condition|)
block|{
name|res
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Adding destination Failed"
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doRemoveDestination
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|res
init|=
literal|true
decl_stmt|;
name|broker
operator|.
name|removeDestination
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|destinationName
argument_list|,
name|destinationType
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ActiveMQDestination
index|[]
name|list
init|=
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinations
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ActiveMQDestination
name|element
range|:
name|list
control|)
block|{
specifier|final
name|Destination
name|destination
init|=
name|broker
operator|.
name|getDestination
argument_list|(
name|element
argument_list|)
decl_stmt|;
if|if
condition|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|equals
argument_list|(
name|destinationName
argument_list|)
condition|)
block|{
name|res
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Removing destination Failed"
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doCheckRemoveActionAfterRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|res
init|=
literal|true
decl_stmt|;
specifier|final
name|ActiveMQDestination
index|[]
name|list
init|=
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinations
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ActiveMQDestination
name|element
range|:
name|list
control|)
block|{
specifier|final
name|Destination
name|destination
init|=
name|broker
operator|.
name|getDestination
argument_list|(
name|element
argument_list|)
decl_stmt|;
if|if
condition|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|equals
argument_list|(
name|destinationName
argument_list|)
condition|)
block|{
name|res
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
literal|"The removed destination is reloaded after restart !"
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|DestinationRemoveRestartTest
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

