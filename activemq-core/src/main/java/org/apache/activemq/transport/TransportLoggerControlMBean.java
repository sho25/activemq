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
comment|/**  * MBean used to manage all of the TransportLoggers at once.  * Avalaible operations:  *  -Enable logging for all TransportLoggers at once.  *  -Disable logging for all TransportLoggers at once.  *    * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|TransportLoggerControlMBean
block|{
comment|/**      * Enable logging for all Transport Loggers at once.      */
specifier|public
name|void
name|enableAllTransportLoggers
parameter_list|()
function_decl|;
comment|/**      * Disable logging for all Transport Loggers at once.      */
specifier|public
name|void
name|disableAllTransportLoggers
parameter_list|()
function_decl|;
comment|/**      * Reloads log4j.properties from the classpath      * @throws Throwable       */
specifier|public
name|void
name|reloadLog4jProperties
parameter_list|()
throws|throws
name|Throwable
function_decl|;
block|}
end_interface

end_unit

