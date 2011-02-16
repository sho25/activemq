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
name|jndi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
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
name|ActiveMQConnectionFactory
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
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|InitialContextTest
extends|extends
name|TestCase
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
name|InitialContextTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testInitialContext
parameter_list|()
throws|throws
name|Exception
block|{
name|InitialContext
name|context
init|=
operator|new
name|InitialContext
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Created context"
argument_list|,
name|context
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|context
operator|.
name|lookup
argument_list|(
literal|"ConnectionFactory"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a ConnectionFactory"
argument_list|,
name|connectionFactory
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created with brokerURL: "
operator|+
name|connectionFactory
operator|.
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUsingStandardJNDIKeys
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
literal|"org.apache.activemq.jndi.ActiveMQInitialContextFactory"
argument_list|)
expr_stmt|;
name|String
name|expected
init|=
literal|"tcp://localhost:65432"
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|InitialContext
name|context
init|=
operator|new
name|InitialContext
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Created context"
argument_list|,
name|context
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|context
operator|.
name|lookup
argument_list|(
literal|"ConnectionFactory"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a ConnectionFactory"
argument_list|,
name|connectionFactory
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the brokerURL should match"
argument_list|,
name|expected
argument_list|,
name|connectionFactory
operator|.
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConnectionFactoryPolicyConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
literal|"org.apache.activemq.jndi.ActiveMQInitialContextFactory"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
literal|"tcp://localhost:65432"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"prefetchPolicy.queuePrefetch"
argument_list|,
literal|"777"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"redeliveryPolicy.maximumRedeliveries"
argument_list|,
literal|"15"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"redeliveryPolicy.backOffMultiplier"
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
name|InitialContext
name|context
init|=
operator|new
name|InitialContext
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Created context"
argument_list|,
name|context
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|context
operator|.
name|lookup
argument_list|(
literal|"ConnectionFactory"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a ConnectionFactory"
argument_list|,
name|connectionFactory
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|777
argument_list|,
name|connectionFactory
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|getQueuePrefetch
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|connectionFactory
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getMaximumRedeliveries
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|32d
argument_list|,
name|connectionFactory
operator|.
name|getRedeliveryPolicy
argument_list|()
operator|.
name|getBackOffMultiplier
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

