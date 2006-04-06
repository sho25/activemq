begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
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
name|Broker
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
name|Connection
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
name|TransportConnector
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
name|TransportServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_comment
comment|/**  * A managed transport connector which can create multiple managed connections  * as clients connect.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ManagedTransportConnector
extends|extends
name|TransportConnector
block|{
specifier|static
name|long
name|nextConnectionId
init|=
literal|1
decl_stmt|;
specifier|private
specifier|final
name|MBeanServer
name|mbeanServer
decl_stmt|;
specifier|private
specifier|final
name|ObjectName
name|connectorName
decl_stmt|;
specifier|public
name|ManagedTransportConnector
parameter_list|(
name|MBeanServer
name|mbeanServer
parameter_list|,
name|ObjectName
name|connectorName
parameter_list|,
name|Broker
name|next
parameter_list|,
name|TransportServer
name|server
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|,
name|server
argument_list|)
expr_stmt|;
name|this
operator|.
name|mbeanServer
operator|=
name|mbeanServer
expr_stmt|;
name|this
operator|.
name|connectorName
operator|=
name|connectorName
expr_stmt|;
block|}
specifier|public
name|ManagedTransportConnector
name|asManagedConnector
parameter_list|(
name|MBeanServer
name|mbeanServer
parameter_list|,
name|ObjectName
name|connectorName
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
return|return
name|this
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|Transport
name|transport
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|connectionId
init|=
literal|""
operator|+
name|getNextConnectionId
argument_list|()
decl_stmt|;
return|return
operator|new
name|ManagedTransportConnection
argument_list|(
name|this
argument_list|,
name|transport
argument_list|,
name|getBroker
argument_list|()
argument_list|,
name|getTaskRunnerFactory
argument_list|()
argument_list|,
name|mbeanServer
argument_list|,
name|connectorName
argument_list|,
name|connectionId
argument_list|)
return|;
block|}
specifier|protected
specifier|static
specifier|synchronized
name|long
name|getNextConnectionId
parameter_list|()
block|{
return|return
name|nextConnectionId
return|;
block|}
block|}
end_class

end_unit

