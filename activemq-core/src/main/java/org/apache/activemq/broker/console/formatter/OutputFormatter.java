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
name|console
operator|.
name|formatter
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectInstance
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
name|javax
operator|.
name|management
operator|.
name|AttributeList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_interface
specifier|public
interface|interface
name|OutputFormatter
block|{
comment|/**      * Retrieve the output stream being used by the formatter      * @return      */
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
function_decl|;
comment|/**      * Print an ObjectInstance format of an mbean      * @param mbean - mbean to print      */
specifier|public
name|void
name|printMBean
parameter_list|(
name|ObjectInstance
name|mbean
parameter_list|)
function_decl|;
comment|/**      * Print an ObjectName format of an mbean      * @param mbean - mbean to print      */
specifier|public
name|void
name|printMBean
parameter_list|(
name|ObjectName
name|mbean
parameter_list|)
function_decl|;
comment|/**      * Print an AttributeList format of an mbean      * @param mbean - mbean to print      */
specifier|public
name|void
name|printMBean
parameter_list|(
name|AttributeList
name|mbean
parameter_list|)
function_decl|;
comment|/**      * Print a Map format of an mbean      * @param mbean - mbean to print      */
specifier|public
name|void
name|printMBean
parameter_list|(
name|Map
name|mbean
parameter_list|)
function_decl|;
comment|/**      * Print a Collection format of mbeans      * @param mbean - collection of mbeans      */
specifier|public
name|void
name|printMBean
parameter_list|(
name|Collection
name|mbean
parameter_list|)
function_decl|;
comment|/**      * Print a Map format of a JMS message      * @param msg      */
specifier|public
name|void
name|printMessage
parameter_list|(
name|Map
name|msg
parameter_list|)
function_decl|;
comment|/**      * Print a Message format of a JMS message      * @param msg - JMS message to print      */
specifier|public
name|void
name|printMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
function_decl|;
comment|/**      * Print a Collection format of JMS messages      * @param msg - collection of JMS messages      */
specifier|public
name|void
name|printMessage
parameter_list|(
name|Collection
name|msg
parameter_list|)
function_decl|;
comment|/**      * Print help messages      * @param helpMsgs - help messages to print      */
specifier|public
name|void
name|printHelp
parameter_list|(
name|String
index|[]
name|helpMsgs
parameter_list|)
function_decl|;
comment|/**      * Print an information message      * @param info - information message to print      */
specifier|public
name|void
name|printInfo
parameter_list|(
name|String
name|info
parameter_list|)
function_decl|;
comment|/**      * Print an exception message      * @param e - exception to print      */
specifier|public
name|void
name|printException
parameter_list|(
name|Exception
name|e
parameter_list|)
function_decl|;
comment|/**      * Print a version information      * @param version - version info to print      */
specifier|public
name|void
name|printVersion
parameter_list|(
name|String
name|version
parameter_list|)
function_decl|;
comment|/**      * Print a generic key value mapping      * @param map to print      */
specifier|public
name|void
name|print
parameter_list|(
name|Map
name|map
parameter_list|)
function_decl|;
comment|/**      * Print a generic array of strings      * @param strings - string array to print      */
specifier|public
name|void
name|print
parameter_list|(
name|String
index|[]
name|strings
parameter_list|)
function_decl|;
comment|/**      * Print a collection of objects      * @param collection - collection to print      */
specifier|public
name|void
name|print
parameter_list|(
name|Collection
name|collection
parameter_list|)
function_decl|;
comment|/**      * Print a java string      * @param string - string to print      */
specifier|public
name|void
name|print
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

