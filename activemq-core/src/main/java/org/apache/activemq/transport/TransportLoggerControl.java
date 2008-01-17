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
name|jmx
operator|.
name|BrokerView
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
name|jmx
operator|.
name|ManagementContext
import|;
end_import

begin_comment
comment|/**  * Implementation of the TransportLoggerControlMBean interface,  * which is an MBean used to control all TransportLoggers at once.  *   * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|TransportLoggerControl
implements|implements
name|TransportLoggerControlMBean
block|{
comment|/**      * Constructor      */
specifier|public
name|TransportLoggerControl
parameter_list|(
name|ManagementContext
name|managementContext
parameter_list|)
block|{     }
comment|// doc comment inherited from TransportLoggerControlMBean
specifier|public
name|void
name|disableAllTransportLoggers
parameter_list|()
block|{
name|TransportLoggerView
operator|.
name|disableAllTransportLoggers
argument_list|()
expr_stmt|;
block|}
comment|// doc comment inherited from TransportLoggerControlMBean
specifier|public
name|void
name|enableAllTransportLoggers
parameter_list|()
block|{
name|TransportLoggerView
operator|.
name|enableAllTransportLoggers
argument_list|()
expr_stmt|;
block|}
comment|//  doc comment inherited from TransportLoggerControlMBean
specifier|public
name|void
name|reloadLog4jProperties
parameter_list|()
throws|throws
name|Throwable
block|{
operator|new
name|BrokerView
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|reloadLog4jProperties
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

