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
name|transport
operator|.
name|stomp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|BrokerContext
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
name|broker
operator|.
name|BrokerServiceAware
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
name|transport
operator|.
name|MutexTransport
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
name|transport
operator|.
name|Transport
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
name|transport
operator|.
name|tcp
operator|.
name|TcpTransportFactory
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
name|IntrospectionSupport
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_comment
comment|/**  * A<a href="http://stomp.codehaus.org/">STOMP</a> transport factory  */
end_comment

begin_class
specifier|public
class|class
name|StompTransportFactory
extends|extends
name|TcpTransportFactory
implements|implements
name|BrokerServiceAware
block|{
specifier|private
name|BrokerContext
name|brokerContext
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|getDefaultWireFormatType
parameter_list|()
block|{
return|return
literal|"stomp"
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|Transport
name|compositeConfigure
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|format
parameter_list|,
name|Map
name|options
parameter_list|)
block|{
name|transport
operator|=
operator|new
name|StompTransportFilter
argument_list|(
name|transport
argument_list|,
name|format
argument_list|,
name|brokerContext
argument_list|)
expr_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|transport
argument_list|,
name|options
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|compositeConfigure
argument_list|(
name|transport
argument_list|,
name|format
argument_list|,
name|options
argument_list|)
return|;
block|}
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|this
operator|.
name|brokerContext
operator|=
name|brokerService
operator|.
name|getBrokerContext
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
specifier|public
name|Transport
name|serverConfigure
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|format
parameter_list|,
name|HashMap
name|options
parameter_list|)
throws|throws
name|Exception
block|{
name|transport
operator|=
name|super
operator|.
name|serverConfigure
argument_list|(
name|transport
argument_list|,
name|format
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|MutexTransport
name|mutex
init|=
name|transport
operator|.
name|narrow
argument_list|(
name|MutexTransport
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|mutex
operator|!=
literal|null
condition|)
block|{
name|mutex
operator|.
name|setSyncOnCommand
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|transport
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Transport
name|createInactivityMonitor
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|format
parameter_list|)
block|{
name|StompInactivityMonitor
name|monitor
init|=
operator|new
name|StompInactivityMonitor
argument_list|(
name|transport
argument_list|,
name|format
argument_list|)
decl_stmt|;
name|StompTransportFilter
name|filter
init|=
operator|(
name|StompTransportFilter
operator|)
name|transport
operator|.
name|narrow
argument_list|(
name|StompTransportFilter
operator|.
name|class
argument_list|)
decl_stmt|;
name|filter
operator|.
name|setInactivityMonitor
argument_list|(
name|monitor
argument_list|)
expr_stmt|;
return|return
name|monitor
return|;
block|}
block|}
end_class

end_unit

