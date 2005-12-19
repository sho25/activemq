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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_comment
comment|/**  * Appends two packets together.  *   * @version $Revision$  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|AppendedPacket
implements|implements
name|Packet
block|{
specifier|private
specifier|final
name|Packet
name|first
decl_stmt|;
specifier|private
specifier|final
name|Packet
name|last
decl_stmt|;
specifier|private
specifier|final
name|int
name|capacity
decl_stmt|;
specifier|private
specifier|final
name|int
name|firstCapacity
decl_stmt|;
specifier|static
specifier|public
name|Packet
name|join
parameter_list|(
name|Packet
name|first
parameter_list|,
name|Packet
name|last
parameter_list|)
block|{
if|if
condition|(
name|first
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
if|if
condition|(
name|last
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
comment|//TODO: this might even be a rejoin of the same continous buffer.
comment|//It would be good if we detected that and avoided just returned the buffer.
return|return
operator|new
name|AppendedPacket
argument_list|(
name|first
operator|.
name|slice
argument_list|()
argument_list|,
name|last
operator|.
name|slice
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|first
operator|.
name|slice
argument_list|()
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|last
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
return|return
name|last
operator|.
name|slice
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|EmptyPacket
operator|.
name|EMPTY_PACKET
return|;
block|}
block|}
block|}
comment|/**      * @deprecated use {@see #join(Packet, Packet)} instead.      */
specifier|public
name|AppendedPacket
parameter_list|(
name|Packet
name|first
parameter_list|,
name|Packet
name|second
parameter_list|)
block|{
name|this
operator|.
name|first
operator|=
name|first
expr_stmt|;
name|this
operator|.
name|last
operator|=
name|second
expr_stmt|;
name|this
operator|.
name|firstCapacity
operator|=
name|first
operator|.
name|capacity
argument_list|()
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|first
operator|.
name|capacity
argument_list|()
operator|+
name|last
operator|.
name|capacity
argument_list|()
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|position
parameter_list|(
name|int
name|position
parameter_list|)
block|{
if|if
condition|(
name|position
operator|<=
name|firstCapacity
condition|)
block|{
name|last
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|first
operator|.
name|position
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|last
operator|.
name|position
argument_list|(
name|position
operator|-
name|firstCapacity
argument_list|)
expr_stmt|;
name|first
operator|.
name|position
argument_list|(
name|firstCapacity
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|limit
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
if|if
condition|(
name|limit
operator|<=
name|firstCapacity
condition|)
block|{
name|last
operator|.
name|limit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|first
operator|.
name|limit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|last
operator|.
name|limit
argument_list|(
name|limit
operator|-
name|firstCapacity
argument_list|)
expr_stmt|;
name|first
operator|.
name|limit
argument_list|(
name|firstCapacity
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Packet
name|slice
parameter_list|()
block|{
return|return
name|join
argument_list|(
name|first
argument_list|,
name|last
argument_list|)
return|;
block|}
specifier|public
name|Packet
name|duplicate
parameter_list|()
block|{
return|return
operator|new
name|AppendedPacket
argument_list|(
name|first
operator|.
name|duplicate
argument_list|()
argument_list|,
name|last
operator|.
name|duplicate
argument_list|()
argument_list|)
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
name|pclazz
init|=
name|cl
operator|.
name|loadClass
argument_list|(
name|Packet
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Class
name|clazz
init|=
name|cl
operator|.
name|loadClass
argument_list|(
name|AppendedPacket
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Constructor
name|constructor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
operator|new
name|Class
index|[]
block|{
name|pclazz
block|,
name|pclazz
block|}
argument_list|)
decl_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
operator|new
name|Object
index|[]
block|{
name|first
operator|.
name|duplicate
argument_list|(
name|cl
argument_list|)
block|,
name|last
operator|.
name|duplicate
argument_list|(
name|cl
argument_list|)
block|}
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
specifier|public
name|void
name|flip
parameter_list|()
block|{
name|limit
argument_list|(
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|position
parameter_list|()
block|{
return|return
name|first
operator|.
name|position
argument_list|()
operator|+
name|last
operator|.
name|position
argument_list|()
return|;
block|}
specifier|public
name|int
name|limit
parameter_list|()
block|{
return|return
name|first
operator|.
name|limit
argument_list|()
operator|+
name|last
operator|.
name|limit
argument_list|()
return|;
block|}
specifier|public
name|int
name|remaining
parameter_list|()
block|{
return|return
name|first
operator|.
name|remaining
argument_list|()
operator|+
name|last
operator|.
name|remaining
argument_list|()
return|;
block|}
specifier|public
name|void
name|rewind
parameter_list|()
block|{
name|first
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|last
operator|.
name|rewind
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasRemaining
parameter_list|()
block|{
return|return
name|first
operator|.
name|hasRemaining
argument_list|()
operator|||
name|last
operator|.
name|hasRemaining
argument_list|()
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|first
operator|.
name|clear
argument_list|()
expr_stmt|;
name|last
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|capacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
specifier|public
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|first
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|last
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|writeTo
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|first
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|last
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.activeio.packet.Packet#read()      */
specifier|public
name|int
name|read
parameter_list|()
block|{
if|if
condition|(
name|first
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
return|return
name|first
operator|.
name|read
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|last
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
return|return
name|last
operator|.
name|read
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|/**      * @see org.activeio.packet.Packet#read(byte[], int, int)      */
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
name|int
name|rc1
init|=
name|first
operator|.
name|read
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc1
operator|==
operator|-
literal|1
condition|)
block|{
name|int
name|rc2
init|=
name|last
operator|.
name|read
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
return|return
operator|(
name|rc2
operator|==
operator|-
literal|1
operator|)
condition|?
operator|-
literal|1
else|:
name|rc2
return|;
block|}
else|else
block|{
name|int
name|rc2
init|=
name|last
operator|.
name|read
argument_list|(
name|data
argument_list|,
name|offset
operator|+
name|rc1
argument_list|,
name|length
operator|-
name|rc1
argument_list|)
decl_stmt|;
return|return
operator|(
name|rc2
operator|==
operator|-
literal|1
operator|)
condition|?
name|rc1
else|:
name|rc1
operator|+
name|rc2
return|;
block|}
block|}
comment|/**      * @see org.activeio.packet.Packet#write(int)      */
specifier|public
name|boolean
name|write
parameter_list|(
name|int
name|data
parameter_list|)
block|{
if|if
condition|(
name|first
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
return|return
name|first
operator|.
name|write
argument_list|(
name|data
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|last
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
return|return
name|last
operator|.
name|write
argument_list|(
name|data
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * @see org.activeio.packet.Packet#write(byte[], int, int)      */
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
name|int
name|rc1
init|=
name|first
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc1
operator|==
operator|-
literal|1
condition|)
block|{
name|int
name|rc2
init|=
name|last
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
return|return
operator|(
name|rc2
operator|==
operator|-
literal|1
operator|)
condition|?
operator|-
literal|1
else|:
name|rc2
return|;
block|}
else|else
block|{
name|int
name|rc2
init|=
name|last
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|offset
operator|+
name|rc1
argument_list|,
name|length
operator|-
name|rc1
argument_list|)
decl_stmt|;
return|return
operator|(
name|rc2
operator|==
operator|-
literal|1
operator|)
condition|?
name|rc1
else|:
name|rc1
operator|+
name|rc2
return|;
block|}
block|}
specifier|public
name|int
name|read
parameter_list|(
name|Packet
name|dest
parameter_list|)
block|{
name|int
name|rc
init|=
name|first
operator|.
name|read
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|rc
operator|+=
name|last
operator|.
name|read
argument_list|(
name|dest
argument_list|)
expr_stmt|;
return|return
name|rc
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
name|Object
name|object
init|=
name|first
operator|.
name|getAdapter
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|object
operator|==
literal|null
condition|)
name|object
operator|=
name|last
operator|.
name|getAdapter
argument_list|(
name|target
argument_list|)
expr_stmt|;
return|return
name|object
return|;
block|}
specifier|public
name|ByteSequence
name|asByteSequence
parameter_list|()
block|{
comment|// TODO: implement me
return|return
literal|null
return|;
block|}
specifier|public
name|byte
index|[]
name|sliceAsBytes
parameter_list|()
block|{
comment|// TODO: implement me
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
name|first
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|last
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

