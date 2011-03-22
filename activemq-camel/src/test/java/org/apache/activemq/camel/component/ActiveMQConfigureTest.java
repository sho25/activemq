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
name|camel
operator|.
name|component
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
name|spring
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
name|pool
operator|.
name|PooledConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|ContextTestSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Endpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|jms
operator|.
name|JmsConsumer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|jms
operator|.
name|JmsEndpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|jms
operator|.
name|JmsProducer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|processor
operator|.
name|CamelLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jms
operator|.
name|core
operator|.
name|JmsTemplate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jms
operator|.
name|listener
operator|.
name|AbstractMessageListenerContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|jms
operator|.
name|connection
operator|.
name|SingleConnectionFactory
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQConfigureTest
extends|extends
name|ContextTestSupport
block|{
specifier|public
name|void
name|testJmsTemplateUsesPoolingConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsEndpoint
name|endpoint
init|=
name|resolveMandatoryEndpoint
argument_list|(
literal|"activemq:test.foo"
argument_list|)
decl_stmt|;
name|JmsProducer
name|producer
init|=
operator|(
name|JmsProducer
operator|)
name|endpoint
operator|.
name|createProducer
argument_list|()
decl_stmt|;
name|JmsTemplate
name|template
init|=
name|assertIsInstanceOf
argument_list|(
name|JmsTemplate
operator|.
name|class
argument_list|,
name|producer
operator|.
name|getInOutTemplate
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"pubSubDomain"
argument_list|,
literal|false
argument_list|,
name|template
operator|.
name|isPubSubDomain
argument_list|()
argument_list|)
expr_stmt|;
name|assertIsInstanceOf
argument_list|(
name|PooledConnectionFactory
operator|.
name|class
argument_list|,
name|template
operator|.
name|getConnectionFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJmsTemplateUsesSingleConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsEndpoint
name|endpoint
init|=
name|resolveMandatoryEndpoint
argument_list|(
literal|"activemq:test.foo?useSingleConnection=true"
argument_list|)
decl_stmt|;
name|JmsProducer
name|producer
init|=
operator|(
name|JmsProducer
operator|)
name|endpoint
operator|.
name|createProducer
argument_list|()
decl_stmt|;
name|JmsTemplate
name|template
init|=
name|assertIsInstanceOf
argument_list|(
name|JmsTemplate
operator|.
name|class
argument_list|,
name|producer
operator|.
name|getInOutTemplate
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"pubSubDomain"
argument_list|,
literal|false
argument_list|,
name|template
operator|.
name|isPubSubDomain
argument_list|()
argument_list|)
expr_stmt|;
name|SingleConnectionFactory
name|connectionFactory
init|=
name|assertIsInstanceOf
argument_list|(
name|SingleConnectionFactory
operator|.
name|class
argument_list|,
name|template
operator|.
name|getConnectionFactory
argument_list|()
argument_list|)
decl_stmt|;
name|assertIsInstanceOf
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|class
argument_list|,
name|connectionFactory
operator|.
name|getTargetConnectionFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJmsTemplateDoesNotUsePoolingConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsEndpoint
name|endpoint
init|=
name|resolveMandatoryEndpoint
argument_list|(
literal|"activemq:test.foo?usePooledConnection=false"
argument_list|)
decl_stmt|;
name|JmsProducer
name|producer
init|=
operator|(
name|JmsProducer
operator|)
name|endpoint
operator|.
name|createProducer
argument_list|()
decl_stmt|;
name|JmsTemplate
name|template
init|=
name|assertIsInstanceOf
argument_list|(
name|JmsTemplate
operator|.
name|class
argument_list|,
name|producer
operator|.
name|getInOutTemplate
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"pubSubDomain"
argument_list|,
literal|false
argument_list|,
name|template
operator|.
name|isPubSubDomain
argument_list|()
argument_list|)
expr_stmt|;
name|assertIsInstanceOf
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|class
argument_list|,
name|template
operator|.
name|getConnectionFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testListenerContainerUsesSpringConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|JmsEndpoint
name|endpoint
init|=
name|resolveMandatoryEndpoint
argument_list|(
literal|"activemq:topic:test.foo"
argument_list|)
decl_stmt|;
name|JmsConsumer
name|consumer
init|=
name|endpoint
operator|.
name|createConsumer
argument_list|(
operator|new
name|CamelLogger
argument_list|()
argument_list|)
decl_stmt|;
name|AbstractMessageListenerContainer
name|listenerContainer
init|=
name|consumer
operator|.
name|getListenerContainer
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"pubSubDomain"
argument_list|,
literal|true
argument_list|,
name|listenerContainer
operator|.
name|isPubSubDomain
argument_list|()
argument_list|)
expr_stmt|;
name|assertIsInstanceOf
argument_list|(
name|PooledConnectionFactory
operator|.
name|class
argument_list|,
name|listenerContainer
operator|.
name|getConnectionFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|JmsEndpoint
name|resolveMandatoryEndpoint
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|Endpoint
name|endpoint
init|=
name|super
operator|.
name|resolveMandatoryEndpoint
argument_list|(
name|uri
argument_list|)
decl_stmt|;
return|return
name|assertIsInstanceOf
argument_list|(
name|JmsEndpoint
operator|.
name|class
argument_list|,
name|endpoint
argument_list|)
return|;
block|}
block|}
end_class

end_unit

