begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|active
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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

begin_comment
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|final
class|class
name|LogFileNode
block|{
specifier|static
specifier|final
specifier|public
name|int
name|SERIALIZED_SIZE
init|=
literal|10
decl_stmt|;
specifier|private
specifier|final
name|LogFile
name|logFile
decl_stmt|;
specifier|private
name|LogFileNode
name|next
decl_stmt|;
comment|/** The id of the log file. */
specifier|private
name|int
name|id
decl_stmt|;
comment|/** Does it have live records in it? */
specifier|private
name|boolean
name|active
init|=
literal|false
decl_stmt|;
comment|/** Is the log file in readonly mode */
specifier|private
name|boolean
name|readOnly
decl_stmt|;
comment|/** The location of the next append offset */
specifier|private
name|int
name|appendOffset
init|=
literal|0
decl_stmt|;
specifier|public
name|LogFileNode
parameter_list|(
name|LogFile
name|logFile
parameter_list|)
block|{
name|this
operator|.
name|logFile
operator|=
name|logFile
expr_stmt|;
block|}
specifier|public
name|LogFile
name|getLogFile
parameter_list|()
block|{
return|return
name|logFile
return|;
block|}
comment|/////////////////////////////////////////////////////////////
comment|//
comment|// Method used to mange the state of the log file.
comment|//
comment|/////////////////////////////////////////////////////////////
specifier|public
name|void
name|activate
parameter_list|(
name|int
name|id
parameter_list|)
block|{
if|if
condition|(
name|active
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Log already active."
argument_list|)
throw|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|readOnly
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|active
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|appendOffset
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|void
name|setReadOnly
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
if|if
condition|(
operator|!
name|active
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Log not active."
argument_list|)
throw|;
name|this
operator|.
name|readOnly
operator|=
name|enable
expr_stmt|;
block|}
specifier|public
name|void
name|deactivate
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|active
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Log already inactive."
argument_list|)
throw|;
name|this
operator|.
name|active
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|id
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|readOnly
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|appendOffset
operator|=
literal|0
expr_stmt|;
name|getLogFile
argument_list|()
operator|.
name|resize
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|isActive
parameter_list|()
block|{
return|return
name|active
return|;
block|}
specifier|public
name|int
name|getAppendOffset
parameter_list|()
block|{
return|return
name|appendOffset
return|;
block|}
specifier|public
name|Location
name|getFirstRecordLocation
parameter_list|()
block|{
if|if
condition|(
name|isActive
argument_list|()
operator|&&
name|appendOffset
operator|>
literal|0
condition|)
return|return
operator|new
name|Location
argument_list|(
name|getId
argument_list|()
argument_list|,
literal|0
argument_list|)
return|;
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
name|readOnly
return|;
block|}
specifier|public
name|void
name|appended
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|appendOffset
operator|+=
name|i
expr_stmt|;
block|}
comment|/////////////////////////////////////////////////////////////
comment|//
comment|// Method used to maintain the list of LogFileNodes used by
comment|// the LogFileManager
comment|//
comment|/////////////////////////////////////////////////////////////
specifier|public
name|LogFileNode
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
specifier|public
name|void
name|setNext
parameter_list|(
name|LogFileNode
name|state
parameter_list|)
block|{
name|next
operator|=
name|state
expr_stmt|;
block|}
specifier|public
name|LogFileNode
name|getNextActive
parameter_list|()
block|{
if|if
condition|(
name|getNext
argument_list|()
operator|.
name|isActive
argument_list|()
condition|)
return|return
name|getNext
argument_list|()
return|;
return|return
literal|null
return|;
block|}
specifier|public
name|LogFileNode
name|getNextInactive
parameter_list|()
block|{
if|if
condition|(
operator|!
name|getNext
argument_list|()
operator|.
name|isActive
argument_list|()
condition|)
return|return
name|getNext
argument_list|()
return|;
return|return
literal|null
return|;
block|}
comment|/**      * @param data      * @throws IOException       */
specifier|public
name|void
name|writeExternal
parameter_list|(
name|DataOutput
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|data
operator|.
name|writeInt
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeBoolean
argument_list|(
name|active
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeBoolean
argument_list|(
name|readOnly
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeInt
argument_list|(
name|appendOffset
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param data      * @throws IOException       */
specifier|public
name|void
name|readExternal
parameter_list|(
name|DataInput
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|id
operator|=
name|data
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|active
operator|=
name|data
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|readOnly
operator|=
name|data
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|appendOffset
operator|=
name|data
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setAppendOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|appendOffset
operator|=
name|offset
expr_stmt|;
block|}
block|}
end_class

end_unit

