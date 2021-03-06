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
name|DestinationsTest
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
name|DestinationsTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testMod
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|configurationSeed
init|=
literal|"destinationTest"
decl_stmt|;
specifier|final
name|String
name|brokerConfig
init|=
name|configurationSeed
operator|+
literal|"-destinations"
decl_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-original"
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerConfig
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
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-add"
argument_list|,
name|SLEEP
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
name|assertTrue
argument_list|(
literal|"contains before"
argument_list|,
name|containsDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"BEFORE"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"contains after"
argument_list|,
name|containsDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"AFTER"
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
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-remove"
argument_list|,
name|SLEEP
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
name|assertTrue
argument_list|(
literal|"contains before"
argument_list|,
name|containsDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"BEFORE"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"contains after"
argument_list|,
name|containsDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"AFTER"
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

