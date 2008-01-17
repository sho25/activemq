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

begin_comment
comment|/**  * MBean to manage a single Transport Logger.  * It can inform if the logger is currently writing to a log file or not,  * by setting the logging property or by using the operations  * enableLogging() and disableLogging().  *   * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|TransportLoggerViewMBean
block|{
comment|/**      * Returns if the managed TransportLogger is currently active      * (writing to a log) or not.      * @return if the managed TransportLogger is currently active      * (writing to a log) or not.      */
specifier|public
name|boolean
name|isLogging
parameter_list|()
function_decl|;
comment|/**      * Enables or disables logging for the managed TransportLogger.      * @param logging Boolean value to enable or disable logging for      * the managed TransportLogger.      * true to enable logging, false to disable logging.      */
specifier|public
name|void
name|setLogging
parameter_list|(
name|boolean
name|logging
parameter_list|)
function_decl|;
comment|/**      * Enables logging for the managed TransportLogger.      */
specifier|public
name|void
name|enableLogging
parameter_list|()
function_decl|;
comment|/**      * Disables logging for the managed TransportLogger.      */
specifier|public
name|void
name|disableLogging
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

