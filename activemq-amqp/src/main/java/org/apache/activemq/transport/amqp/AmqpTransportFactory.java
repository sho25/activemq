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
name|amqp
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

begin_comment
comment|/**  * A<a href="http://amqp.org/">AMQP</a> transport factory  */
end_comment

begin_class
specifier|public
class|class
name|AmqpTransportFactory
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
literal|"amqp"
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
name|AmqpTransportFilter
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
comment|// strip off the mutex transport.
if|if
condition|(
name|transport
operator|instanceof
name|MutexTransport
condition|)
block|{
name|transport
operator|=
operator|(
operator|(
name|MutexTransport
operator|)
name|transport
operator|)
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
comment|//        MutexTransport mutex = transport.narrow(MutexTransport.class);
comment|//        if (mutex != null) {
comment|//            mutex.setSyncOnCommand(true);
comment|//        }
return|return
name|transport
return|;
block|}
comment|//    @Override
comment|//    protected Transport createInactivityMonitor(Transport transport, WireFormat format) {
comment|//        AmqpInactivityMonitor monitor = new AmqpInactivityMonitor(transport, format);
comment|//
comment|//        AmqpTransportFilter filter = transport.narrow(AmqpTransportFilter.class);
comment|//        filter.setInactivityMonitor(monitor);
comment|//
comment|//        return monitor;
comment|//    }
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseInactivityMonitor
parameter_list|(
name|Transport
name|transport
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit
