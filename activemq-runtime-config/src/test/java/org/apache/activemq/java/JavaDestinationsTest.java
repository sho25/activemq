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
name|java
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
name|util
operator|.
name|Arrays
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
name|TimeUnit
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
name|RuntimeConfigTestSupport
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
name|command
operator|.
name|ActiveMQDestination
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
name|ActiveMQQueue
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
name|plugin
operator|.
name|java
operator|.
name|JavaRuntimeConfigurationBroker
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
name|plugin
operator|.
name|java
operator|.
name|JavaRuntimeConfigurationPlugin
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
name|Wait
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

begin_class
specifier|public
class|class
name|JavaDestinationsTest
extends|extends
name|RuntimeConfigTestSupport
block|{
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JavaDestinationsTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|JavaRuntimeConfigurationBroker
name|javaConfigBroker
decl_stmt|;
specifier|public
name|void
name|startBroker
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
name|brokerService
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
operator|new
name|JavaRuntimeConfigurationPlugin
argument_list|()
block|}
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
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|javaConfigBroker
operator|=
operator|(
name|JavaRuntimeConfigurationBroker
operator|)
name|brokerService
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|JavaRuntimeConfigurationBroker
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMod
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setDestinations
argument_list|(
operator|new
name|ActiveMQDestination
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ORIGINAL"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|printDestinations
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"contains original"
argument_list|,
name|containsDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ORIGINAL"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding destinations"
argument_list|)
expr_stmt|;
comment|//apply new config
name|javaConfigBroker
operator|.
name|setDestinations
argument_list|(
operator|new
name|ActiveMQDestination
index|[]
block|{
operator|new
name|ActiveMQTopic
argument_list|(
literal|"BEFORE"
argument_list|)
block|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ORIGINAL"
argument_list|)
block|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"AFTER"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|printDestinations
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"contains destinations"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|containsDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ORIGINAL"
argument_list|)
argument_list|)
operator|&&
name|containsDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"BEFORE"
argument_list|)
argument_list|)
operator|&&
name|containsDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"AFTER"
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|SLEEP
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing destinations"
argument_list|)
expr_stmt|;
comment|//apply new config
name|javaConfigBroker
operator|.
name|setDestinations
argument_list|(
operator|new
name|ActiveMQDestination
index|[]
block|{
operator|new
name|ActiveMQTopic
argument_list|(
literal|"BEFORE"
argument_list|)
block|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"AFTER"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|printDestinations
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"contains destinations"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|containsDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"ORIGINAL"
argument_list|)
argument_list|)
operator|&&
name|containsDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"BEFORE"
argument_list|)
argument_list|)
operator|&&
name|containsDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"AFTER"
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|SLEEP
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|boolean
name|containsDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|brokerService
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinations
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|destination
argument_list|)
return|;
block|}
specifier|protected
name|void
name|printDestinations
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|brokerService
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQDestination
name|destination
range|:
name|destinations
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker destination: "
operator|+
name|destination
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
