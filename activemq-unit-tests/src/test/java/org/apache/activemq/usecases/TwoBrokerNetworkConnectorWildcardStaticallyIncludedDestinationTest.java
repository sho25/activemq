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
name|usecases
package|;
end_package

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
name|network
operator|.
name|NetworkConnector
import|;
end_import

begin_class
specifier|public
class|class
name|TwoBrokerNetworkConnectorWildcardStaticallyIncludedDestinationTest
extends|extends
name|AbstractTwoBrokerNetworkConnectorWildcardIncludedDestinationTestSupport
block|{
specifier|protected
name|void
name|addIncludedDestination
parameter_list|(
name|NetworkConnector
name|nc
parameter_list|)
block|{
name|nc
operator|.
name|addExcludedDestination
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
literal|"local.>"
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|nc
operator|.
name|addExcludedDestination
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
literal|"local.>"
argument_list|,
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|nc
operator|.
name|addExcludedDestination
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
literal|"Consumer.*.local.>"
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|nc
operator|.
name|addStaticallyIncludedDestination
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
literal|"global.>"
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|nc
operator|.
name|addStaticallyIncludedDestination
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
literal|"global.>"
argument_list|,
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|nc
operator|.
name|addStaticallyIncludedDestination
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
literal|"Consumer.*.global.>"
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
