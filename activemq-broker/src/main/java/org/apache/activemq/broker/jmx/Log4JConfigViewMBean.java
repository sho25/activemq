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
name|broker
operator|.
name|jmx
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Log4J Configuration Management MBean used to alter the runtime log levels  * or force a reload of the Log4J configuration file.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Log4JConfigViewMBean
block|{
comment|/**      * Get the log level for the root logger      *      * @returns the current log level of the root logger.      *      * @throws Exception if an error occurs while getting the root level.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Returns the current logging level of the root logger."
argument_list|)
name|String
name|getRootLogLevel
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Get the log level for the root logger      *      * @param level      *        the new level to assign to the root logger.      *      * @throws Exception if an error occurs while setting the root level.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Sets the current logging level of the root logger."
argument_list|)
name|void
name|setRootLogLevel
parameter_list|(
name|String
name|level
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * list of all the logger names and their levels      *      * @returns a List of all known loggers names.      *      * @throws Exception if an error occurs while getting the loggers.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"List of all loggers that are available for configuration."
argument_list|)
name|List
argument_list|<
name|String
argument_list|>
name|getLoggers
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Get the log level for a given logger      *      * @param loggerName      *        the name of the logger whose level should be queried.      *      * @returns the current log level of the given logger.      *      * @throws Exception if an error occurs while getting the log level.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Returns the current logging level of a named logger."
argument_list|)
name|String
name|getLogLevel
parameter_list|(
name|String
name|loggerName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Set the log level for a given logger      *      * @param loggerName      *        the name of the logger whose level is to be adjusted.      * @param level      *        the new level to assign the given logger.      *      * @throws Exception if an error occurs while setting the log level.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Sets the logging level for the named logger."
argument_list|)
name|void
name|setLogLevel
parameter_list|(
name|String
name|loggerName
parameter_list|,
name|String
name|level
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Reloads log4j.properties from the classpath.      *      * @throws Exception if an error occurs trying to reload the config file.      */
annotation|@
name|MBeanInfo
argument_list|(
name|value
operator|=
literal|"Reloads log4j.properties from the classpath."
argument_list|)
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

