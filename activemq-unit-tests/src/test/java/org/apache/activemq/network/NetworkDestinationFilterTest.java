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
name|network
package|;
end_package

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
name|advisory
operator|.
name|AdvisorySupport
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
name|ActiveMQTempQueue
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

begin_class
specifier|public
class|class
name|NetworkDestinationFilterTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|NetworkBridgeConfiguration
name|config
init|=
operator|new
name|NetworkBridgeConfiguration
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|AdvisorySupport
operator|.
name|CONSUMER_ADVISORY_TOPIC_PREFIX
operator|+
literal|">"
argument_list|,
name|config
operator|.
name|getDestinationFilter
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|dests
init|=
operator|new
name|ArrayList
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
decl_stmt|;
name|config
operator|.
name|setDynamicallyIncludedDestinations
argument_list|(
name|dests
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AdvisorySupport
operator|.
name|CONSUMER_ADVISORY_TOPIC_PREFIX
operator|+
literal|">"
argument_list|,
name|config
operator|.
name|getDestinationFilter
argument_list|()
argument_list|)
expr_stmt|;
name|dests
operator|.
name|add
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.>"
argument_list|)
argument_list|)
expr_stmt|;
name|dests
operator|.
name|add
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST.>"
argument_list|)
argument_list|)
expr_stmt|;
name|dests
operator|.
name|add
argument_list|(
operator|new
name|ActiveMQTempQueue
argument_list|(
literal|"TEST.>"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|prefix
init|=
name|AdvisorySupport
operator|.
name|CONSUMER_ADVISORY_TOPIC_PREFIX
decl_stmt|;
name|assertEquals
argument_list|(
name|prefix
operator|+
literal|"Queue.TEST.>,"
operator|+
name|prefix
operator|+
literal|"Topic.TEST.>"
argument_list|,
name|config
operator|.
name|getDestinationFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

