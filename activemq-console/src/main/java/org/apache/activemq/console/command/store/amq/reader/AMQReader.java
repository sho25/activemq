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
operator|.
name|command
operator|.
name|store
operator|.
name|amq
operator|.
name|reader
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|InvalidSelectorException
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|DataStructure
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
name|filter
operator|.
name|BooleanExpression
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
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|AsyncDataManager
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
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|Location
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
name|openwire
operator|.
name|OpenWireFormat
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
name|selector
operator|.
name|SelectorParser
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
name|ByteSequence
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
comment|/**  * Reads and iterates through data log files for the AMQMessage Store  *   */
end_comment

begin_class
specifier|public
class|class
name|AMQReader
implements|implements
name|Iterable
argument_list|<
name|Message
argument_list|>
block|{
specifier|private
name|AsyncDataManager
name|dataManager
decl_stmt|;
specifier|private
name|WireFormat
name|wireFormat
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
specifier|private
name|File
name|file
decl_stmt|;
specifier|private
name|BooleanExpression
name|expression
decl_stmt|;
comment|/**      * List all the data files in a directory      * @param directory      * @return      * @throws IOException      */
specifier|public
specifier|static
name|Set
argument_list|<
name|File
argument_list|>
name|listDataFiles
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|File
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|directory
operator|==
literal|null
operator|||
operator|!
name|directory
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|directory
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid Directory "
operator|+
name|directory
argument_list|)
throw|;
block|}
name|AsyncDataManager
name|dataManager
init|=
operator|new
name|AsyncDataManager
argument_list|()
decl_stmt|;
name|dataManager
operator|.
name|setDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|dataManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|File
argument_list|>
name|set
init|=
name|dataManager
operator|.
name|getFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|set
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
name|dataManager
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Create the AMQReader to read a directory of amq data logs - or an      * individual data log file      *       * @param file the directory - or file      * @throws IOException       * @throws InvalidSelectorException       * @throws IOException      * @throws InvalidSelectorException       */
specifier|public
name|AMQReader
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|InvalidSelectorException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|file
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create the AMQReader to read a directory of amq data logs - or an      * individual data log file      *       * @param file the directory - or file      * @param selector the JMS selector or null to select all      * @throws IOException      * @throws InvalidSelectorException       */
specifier|public
name|AMQReader
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidSelectorException
block|{
name|String
name|str
init|=
name|selector
operator|!=
literal|null
condition|?
name|selector
operator|.
name|trim
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|str
operator|!=
literal|null
operator|&&
name|str
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|expression
operator|=
name|SelectorParser
operator|.
name|parse
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
name|dataManager
operator|=
operator|new
name|AsyncDataManager
argument_list|()
expr_stmt|;
name|dataManager
operator|.
name|setArchiveDataLogs
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|dataManager
operator|.
name|setDirectory
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dataManager
operator|.
name|setDirectory
argument_list|(
name|file
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
name|dataManager
operator|.
name|setDirectoryArchive
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
block|}
name|dataManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Message
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|AMQIterator
argument_list|(
name|this
argument_list|,
name|this
operator|.
name|expression
argument_list|)
return|;
block|}
specifier|protected
name|MessageLocation
name|getNextMessage
parameter_list|(
name|MessageLocation
name|lastLocation
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|file
operator|!=
literal|null
condition|)
block|{
return|return
name|getInternalNextMessage
argument_list|(
name|this
operator|.
name|file
argument_list|,
name|lastLocation
argument_list|)
return|;
block|}
return|return
name|getInternalNextMessage
argument_list|(
name|lastLocation
argument_list|)
return|;
block|}
specifier|private
name|MessageLocation
name|getInternalNextMessage
parameter_list|(
name|MessageLocation
name|lastLocation
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|IOException
block|{
return|return
name|getInternalNextMessage
argument_list|(
literal|null
argument_list|,
name|lastLocation
argument_list|)
return|;
block|}
specifier|private
name|MessageLocation
name|getInternalNextMessage
parameter_list|(
name|File
name|file
parameter_list|,
name|MessageLocation
name|lastLocation
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|IOException
block|{
name|MessageLocation
name|result
init|=
name|lastLocation
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|setMessage
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|Message
name|message
init|=
literal|null
decl_stmt|;
name|Location
name|pos
init|=
name|lastLocation
operator|!=
literal|null
condition|?
name|lastLocation
operator|.
name|getLocation
argument_list|()
else|:
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|pos
operator|=
name|getNextLocation
argument_list|(
name|file
argument_list|,
name|pos
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|message
operator|=
name|getMessage
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|MessageLocation
argument_list|()
expr_stmt|;
block|}
name|result
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|result
operator|.
name|setLocation
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|==
literal|null
operator|&&
name|message
operator|==
literal|null
condition|)
block|{
name|result
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|setLocation
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|Location
name|getNextLocation
parameter_list|(
name|File
name|file
parameter_list|,
name|Location
name|last
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|IOException
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
return|return
name|dataManager
operator|.
name|getNextLocation
argument_list|(
name|file
argument_list|,
name|last
argument_list|,
literal|true
argument_list|)
return|;
block|}
return|return
name|dataManager
operator|.
name|getNextLocation
argument_list|(
name|last
argument_list|)
return|;
block|}
specifier|private
name|Message
name|getMessage
parameter_list|(
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteSequence
name|data
init|=
name|dataManager
operator|.
name|read
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|DataStructure
name|c
init|=
operator|(
name|DataStructure
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
name|data
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|instanceof
name|Message
condition|)
block|{
return|return
operator|(
name|Message
operator|)
name|c
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

