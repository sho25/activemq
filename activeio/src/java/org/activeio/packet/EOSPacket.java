begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2004 Hiram Chirino  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not  * use this file except in compliance with the License. You may obtain a copy of  * the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|packet
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|ByteSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|Packet
import|;
end_import

begin_comment
comment|/**  * Provides a Packet implementation that is used to represent the end of a stream.  *   * @version $Revision$  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|EOSPacket
implements|implements
name|Packet
block|{
specifier|static
specifier|final
specifier|public
name|EOSPacket
name|EOS_PACKET
init|=
operator|new
name|EOSPacket
argument_list|()
decl_stmt|;
specifier|private
name|EOSPacket
parameter_list|()
block|{     }
specifier|public
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|writeTo
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|int
name|position
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
specifier|public
name|void
name|position
parameter_list|(
name|int
name|position
parameter_list|)
block|{     }
specifier|public
name|int
name|limit
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|void
name|limit
parameter_list|(
name|int
name|limit
parameter_list|)
block|{     }
specifier|public
name|void
name|flip
parameter_list|()
block|{     }
specifier|public
name|int
name|remaining
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|void
name|rewind
parameter_list|()
block|{     }
specifier|public
name|boolean
name|hasRemaining
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{     }
specifier|public
name|int
name|capacity
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|Packet
name|slice
parameter_list|()
block|{
return|return
name|this
return|;
block|}
specifier|public
name|Packet
name|duplicate
parameter_list|()
block|{
return|return
name|this
return|;
block|}
specifier|public
name|Object
name|duplicate
parameter_list|(
name|ClassLoader
name|cl
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Class
name|clazz
init|=
name|cl
operator|.
name|loadClass
argument_list|(
name|EOSPacket
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|clazz
operator|.
name|getField
argument_list|(
literal|"EOS_PACKET"
argument_list|)
operator|.
name|get
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Could not duplicate packet in a different classloader: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.activeio.Packet#read()      */
specifier|public
name|int
name|read
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|/**      * @see org.activeio.Packet#read(byte[], int, int)      */
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|/**      * @see org.activeio.Packet#write(int)      */
specifier|public
name|boolean
name|write
parameter_list|(
name|int
name|data
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @see org.activeio.Packet#write(byte[], int, int)      */
specifier|public
name|int
name|write
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|ByteSequence
name|asByteSequence
parameter_list|()
block|{
return|return
name|EmptyPacket
operator|.
name|EMPTY_BYTE_SEQUENCE
return|;
block|}
specifier|public
name|byte
index|[]
name|sliceAsBytes
parameter_list|()
block|{
return|return
name|EmptyPacket
operator|.
name|EMPTY_BYTE_ARRAY
return|;
block|}
comment|/**      * @param dest      * @return the number of bytes read into the dest.      */
specifier|public
name|int
name|read
parameter_list|(
name|Packet
name|dest
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{position="
operator|+
name|position
argument_list|()
operator|+
literal|",limit="
operator|+
name|limit
argument_list|()
operator|+
literal|",capacity="
operator|+
name|capacity
argument_list|()
operator|+
literal|"}"
return|;
block|}
specifier|public
name|Object
name|getAdapter
parameter_list|(
name|Class
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{             }
block|}
end_class

end_unit

