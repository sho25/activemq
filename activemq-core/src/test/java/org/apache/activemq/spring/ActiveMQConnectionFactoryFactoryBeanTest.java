begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|spring
package|;
end_package

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
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQConnectionFactoryFactoryBeanTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ActiveMQConnectionFactoryFactoryBeanTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactoryFactoryBean
name|factory
decl_stmt|;
specifier|public
name|void
name|testSingleTcpURL
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|.
name|setTcpHostAndPort
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
expr_stmt|;
name|assertCreatedURL
argument_list|(
literal|"failover:(tcp://localhost:61616)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSingleTcpURLWithInactivityTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|.
name|setTcpHostAndPort
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setMaxInactivityDuration
argument_list|(
literal|60000L
argument_list|)
expr_stmt|;
name|assertCreatedURL
argument_list|(
literal|"failover:(tcp://localhost:61616?wireFormat.maxInactivityDuration=60000)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSingleTcpURLWithInactivityTimeoutAndTcpNoDelay
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|.
name|setTcpHostAndPort
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setMaxInactivityDuration
argument_list|(
literal|50000L
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setTcpProperties
argument_list|(
literal|"tcpNoDelayEnabled=true"
argument_list|)
expr_stmt|;
name|assertCreatedURL
argument_list|(
literal|"failover:(tcp://localhost:61616?wireFormat.maxInactivityDuration=50000&tcpNoDelayEnabled=true)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSingleTcpURLWithInactivityTimeoutAndMaxReconnectDelay
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|.
name|setTcpHostAndPort
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setMaxInactivityDuration
argument_list|(
literal|60000L
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setMaxReconnectDelay
argument_list|(
literal|50000L
argument_list|)
expr_stmt|;
name|assertCreatedURL
argument_list|(
literal|"failover:(tcp://localhost:61616?wireFormat.maxInactivityDuration=60000)?maxReconnectDelay=50000"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSingleTcpURLWithInactivityTimeoutAndMaxReconnectDelayAndFailoverProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|.
name|setTcpHostAndPort
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setMaxInactivityDuration
argument_list|(
literal|40000L
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setMaxReconnectDelay
argument_list|(
literal|30000L
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setFailoverProperties
argument_list|(
literal|"useExponentialBackOff=false"
argument_list|)
expr_stmt|;
name|assertCreatedURL
argument_list|(
literal|"failover:(tcp://localhost:61616?wireFormat.maxInactivityDuration=40000)?maxReconnectDelay=30000&useExponentialBackOff=false"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultipleTcpURLsWithInactivityTimeoutAndMaxReconnectDelayAndFailoverProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|.
name|setTcpHostAndPorts
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"tcp://localhost:61618"
block|,
literal|"tcp://foo:61619"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setMaxInactivityDuration
argument_list|(
literal|40000L
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setMaxReconnectDelay
argument_list|(
literal|30000L
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setFailoverProperties
argument_list|(
literal|"useExponentialBackOff=false"
argument_list|)
expr_stmt|;
name|assertCreatedURL
argument_list|(
literal|"failover:(tcp://localhost:61618?wireFormat.maxInactivityDuration=40000,tcp://foo:61619?wireFormat.maxInactivityDuration=40000)?maxReconnectDelay=30000&useExponentialBackOff=false"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertCreatedURL
parameter_list|(
name|String
name|expectedURL
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|url
init|=
name|factory
operator|.
name|getBrokerURL
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Generated URL: "
operator|+
name|url
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"URL"
argument_list|,
name|expectedURL
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|factory
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Value should be an ActiveMQConnectionFactory"
argument_list|,
name|value
operator|instanceof
name|ActiveMQConnectionFactory
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|value
decl_stmt|;
name|String
name|brokerURL
init|=
name|connectionFactory
operator|.
name|getBrokerURL
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"brokerURL"
argument_list|,
name|expectedURL
argument_list|,
name|brokerURL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactoryFactoryBean
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

