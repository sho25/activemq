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
name|console
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|javax
operator|.
name|jms
operator|.
name|Message
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|console
operator|.
name|formatter
operator|.
name|OutputFormatter
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|CommandContext
block|{
specifier|private
name|OutputFormatter
name|formatter
decl_stmt|;
comment|/**      * Retrieve the output stream being used by the global formatter      *      * @return formatter's output stream      */
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
return|return
name|formatter
operator|.
name|getOutputStream
argument_list|()
return|;
block|}
comment|/**      * Print an ObjectInstance format of an mbean      *      * @param mbean - mbean to print      */
specifier|public
name|void
name|printMBean
parameter_list|(
name|ObjectInstance
name|mbean
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printMBean
argument_list|(
name|mbean
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print an ObjectName format of an mbean      *      * @param mbean - mbean to print      */
specifier|public
name|void
name|printMBean
parameter_list|(
name|ObjectName
name|mbean
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printMBean
argument_list|(
name|mbean
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print an AttributeList format of an mbean      *      * @param mbean - mbean to print      */
specifier|public
name|void
name|printMBean
parameter_list|(
name|AttributeList
name|mbean
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printMBean
argument_list|(
name|mbean
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print a Map format of an mbean      *      * @param mbean      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|void
name|printMBean
parameter_list|(
name|Map
name|mbean
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printMBean
argument_list|(
name|mbean
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print a Collection format of mbeans      *      * @param mbean - collection of mbeans      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|void
name|printMBean
parameter_list|(
name|Collection
name|mbean
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printMBean
argument_list|(
name|mbean
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print a Map format of a JMS message      *      * @param msg      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|void
name|printMessage
parameter_list|(
name|Map
name|msg
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print a Message format of a JMS message      *      * @param msg - JMS message to print      */
specifier|public
name|void
name|printMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print a collection of JMS messages      *      * @param msg - collection of JMS messages      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|void
name|printMessage
parameter_list|(
name|Collection
name|msg
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print help messages      *      * @param helpMsgs - help messages to print      */
specifier|public
name|void
name|printHelp
parameter_list|(
name|String
index|[]
name|helpMsgs
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printHelp
argument_list|(
name|helpMsgs
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print an information message      *      * @param info - information message to print      */
specifier|public
name|void
name|printInfo
parameter_list|(
name|String
name|info
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print an exception message      *      * @param e - exception to print      */
specifier|public
name|void
name|printException
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print a version information      *      * @param version - version info to print      */
specifier|public
name|void
name|printVersion
parameter_list|(
name|String
name|version
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|printVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print a generic key value mapping      *      * @param map to print      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|void
name|print
parameter_list|(
name|Map
name|map
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|print
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print a generic array of strings      *      * @param strings - string array to print      */
specifier|public
name|void
name|print
parameter_list|(
name|String
index|[]
name|strings
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|print
argument_list|(
name|strings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print a collection of objects      *      * @param collection - collection to print      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|public
name|void
name|print
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|print
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
comment|/**      * Print a java string      *      * @param string - string to print      */
specifier|public
name|void
name|print
parameter_list|(
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
name|formatter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No OutputFormatter specified. Use GlobalWriter.instantiate(OutputFormatter)."
argument_list|)
throw|;
block|}
name|formatter
operator|.
name|print
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OutputFormatter
name|getFormatter
parameter_list|()
block|{
return|return
name|formatter
return|;
block|}
specifier|public
name|void
name|setFormatter
parameter_list|(
name|OutputFormatter
name|formatter
parameter_list|)
block|{
name|this
operator|.
name|formatter
operator|=
name|formatter
expr_stmt|;
block|}
block|}
end_class

end_unit

