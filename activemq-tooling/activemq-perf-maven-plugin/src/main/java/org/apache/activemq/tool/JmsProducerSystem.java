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
name|tool
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|tool
operator|.
name|properties
operator|.
name|JmsClientProperties
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
name|tool
operator|.
name|properties
operator|.
name|JmsClientSystemProperties
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
name|tool
operator|.
name|properties
operator|.
name|JmsProducerProperties
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
name|tool
operator|.
name|properties
operator|.
name|JmsProducerSystemProperties
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
name|tool
operator|.
name|sampler
operator|.
name|ThroughputSamplerTask
import|;
end_import

begin_class
specifier|public
class|class
name|JmsProducerSystem
extends|extends
name|AbstractJmsClientSystem
block|{
specifier|protected
name|JmsProducerSystemProperties
name|sysTest
init|=
operator|new
name|JmsProducerSystemProperties
argument_list|()
decl_stmt|;
specifier|protected
name|JmsProducerProperties
name|producer
init|=
operator|new
name|JmsProducerProperties
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|JmsClientSystemProperties
name|getSysTest
parameter_list|()
block|{
return|return
name|sysTest
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSysTest
parameter_list|(
name|JmsClientSystemProperties
name|sysTestProps
parameter_list|)
block|{
name|sysTest
operator|=
operator|(
name|JmsProducerSystemProperties
operator|)
name|sysTestProps
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|JmsClientProperties
name|getJmsClientProperties
parameter_list|()
block|{
return|return
name|getProducer
argument_list|()
return|;
block|}
specifier|public
name|JmsProducerProperties
name|getProducer
parameter_list|()
block|{
return|return
name|producer
return|;
block|}
specifier|public
name|void
name|setProducer
parameter_list|(
name|JmsProducerProperties
name|producer
parameter_list|)
block|{
name|this
operator|.
name|producer
operator|=
name|producer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ClientRunBasis
name|getClientRunBasis
parameter_list|()
block|{
assert|assert
operator|(
name|producer
operator|!=
literal|null
operator|)
assert|;
return|return
name|ClientRunBasis
operator|.
name|valueOf
argument_list|(
name|producer
operator|.
name|getSendType
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|long
name|getClientRunDuration
parameter_list|()
block|{
return|return
name|producer
operator|.
name|getSendDuration
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runJmsClient
parameter_list|(
name|String
name|clientName
parameter_list|,
name|int
name|clientDestIndex
parameter_list|,
name|int
name|clientDestCount
parameter_list|)
block|{
name|ThroughputSamplerTask
name|sampler
init|=
name|getTpSampler
argument_list|()
decl_stmt|;
name|JmsProducerClient
name|producerClient
init|=
operator|new
name|JmsProducerClient
argument_list|(
name|producer
argument_list|,
name|jmsConnFactory
argument_list|)
decl_stmt|;
name|producerClient
operator|.
name|setClientName
argument_list|(
name|clientName
argument_list|)
expr_stmt|;
if|if
condition|(
name|sampler
operator|!=
literal|null
condition|)
block|{
name|sampler
operator|.
name|registerClient
argument_list|(
name|producerClient
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|producerClient
operator|.
name|sendMessages
argument_list|(
name|clientDestIndex
argument_list|,
name|clientDestCount
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|JmsProducerSystem
name|sys
init|=
operator|new
name|JmsProducerSystem
argument_list|()
decl_stmt|;
name|sys
operator|.
name|configureProperties
argument_list|(
name|AbstractJmsClientSystem
operator|.
name|parseStringArgs
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|sys
operator|.
name|runSystemTest
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

