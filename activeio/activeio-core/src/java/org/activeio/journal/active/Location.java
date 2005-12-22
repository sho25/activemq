begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
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

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|journal
operator|.
name|RecordLocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|Packet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|PacketData
import|;
end_import

begin_comment
comment|/**  * Defines a where a record can be located in the Journal.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|Location
implements|implements
name|RecordLocation
block|{
specifier|static
specifier|final
specifier|public
name|int
name|SERIALIZED_SIZE
init|=
literal|8
decl_stmt|;
specifier|final
specifier|private
name|int
name|logFileId
decl_stmt|;
specifier|final
specifier|private
name|int
name|logFileOffset
decl_stmt|;
specifier|public
name|Location
parameter_list|(
name|int
name|logFileId
parameter_list|,
name|int
name|fileOffset
parameter_list|)
block|{
name|this
operator|.
name|logFileId
operator|=
name|logFileId
expr_stmt|;
name|this
operator|.
name|logFileOffset
operator|=
name|fileOffset
expr_stmt|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|int
name|rc
init|=
name|logFileId
operator|-
operator|(
operator|(
name|Location
operator|)
name|o
operator|)
operator|.
name|logFileId
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|0
condition|)
return|return
name|rc
return|;
return|return
name|logFileOffset
operator|-
operator|(
operator|(
name|Location
operator|)
name|o
operator|)
operator|.
name|logFileOffset
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|logFileOffset
operator|^
name|logFileId
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|Location
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|Location
name|rl
init|=
operator|(
name|Location
operator|)
name|o
decl_stmt|;
return|return
name|rl
operator|.
name|logFileId
operator|==
name|this
operator|.
name|logFileId
operator|&&
name|rl
operator|.
name|logFileOffset
operator|==
name|this
operator|.
name|logFileOffset
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|""
operator|+
name|logFileId
operator|+
literal|":"
operator|+
name|logFileOffset
return|;
block|}
specifier|public
name|int
name|getLogFileId
parameter_list|()
block|{
return|return
name|logFileId
return|;
block|}
specifier|public
name|int
name|getLogFileOffset
parameter_list|()
block|{
return|return
name|logFileOffset
return|;
block|}
specifier|public
name|void
name|writeToPacket
parameter_list|(
name|Packet
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|PacketData
name|data
init|=
operator|new
name|PacketData
argument_list|(
name|packet
argument_list|)
decl_stmt|;
name|data
operator|.
name|writeInt
argument_list|(
name|logFileId
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeInt
argument_list|(
name|logFileOffset
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|writeToDataOutput
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
name|logFileId
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeInt
argument_list|(
name|logFileOffset
argument_list|)
expr_stmt|;
block|}
specifier|static
specifier|public
name|Location
name|readFromPacket
parameter_list|(
name|Packet
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|PacketData
name|data
init|=
operator|new
name|PacketData
argument_list|(
name|packet
argument_list|)
decl_stmt|;
return|return
operator|new
name|Location
argument_list|(
name|data
operator|.
name|readInt
argument_list|()
argument_list|,
name|data
operator|.
name|readInt
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Location
name|readFromDataInput
parameter_list|(
name|DataInput
name|data
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Location
argument_list|(
name|data
operator|.
name|readInt
argument_list|()
argument_list|,
name|data
operator|.
name|readInt
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

