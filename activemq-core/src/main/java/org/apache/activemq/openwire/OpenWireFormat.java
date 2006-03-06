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
name|openwire
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|ByteArrayOutputStream
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

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|PacketData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|PacketToInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|command
operator|.
name|ClassLoading
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|command
operator|.
name|WireFormat
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
name|ByteArrayPacket
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
name|CommandTypes
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
name|command
operator|.
name|MarshallAware
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|OpenWireFormat
implements|implements
name|WireFormat
block|{
specifier|static
specifier|final
name|byte
name|NULL_TYPE
init|=
name|CommandTypes
operator|.
name|NULL
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MARSHAL_CACHE_SIZE
init|=
name|Short
operator|.
name|MAX_VALUE
operator|/
literal|2
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MARSHAL_CACHE_PREFERED_SIZE
init|=
name|MARSHAL_CACHE_SIZE
operator|-
literal|100
decl_stmt|;
specifier|private
name|DataStreamMarshaller
name|dataMarshallers
index|[]
decl_stmt|;
specifier|private
name|int
name|version
decl_stmt|;
specifier|private
name|boolean
name|stackTraceEnabled
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|tcpNoDelayEnabled
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|cacheEnabled
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|tightEncodingEnabled
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|prefixPacketSize
init|=
literal|true
decl_stmt|;
specifier|private
name|HashMap
name|marshallCacheMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|short
name|nextMarshallCacheIndex
init|=
literal|0
decl_stmt|;
specifier|private
name|short
name|nextMarshallCacheEvictionIndex
init|=
literal|0
decl_stmt|;
specifier|private
name|DataStructure
name|marshallCache
index|[]
init|=
operator|new
name|DataStructure
index|[
name|MARSHAL_CACHE_SIZE
index|]
decl_stmt|;
specifier|private
name|DataStructure
name|unmarshallCache
index|[]
init|=
operator|new
name|DataStructure
index|[
name|MARSHAL_CACHE_SIZE
index|]
decl_stmt|;
specifier|public
name|OpenWireFormat
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OpenWireFormat
parameter_list|(
name|boolean
name|cacheEnabled
parameter_list|)
block|{
name|setVersion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|setCacheEnabled
argument_list|(
name|cacheEnabled
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|version
operator|^
operator|(
name|cacheEnabled
condition|?
literal|0x10000000
else|:
literal|0x20000000
operator|)
operator|^
operator|(
name|stackTraceEnabled
condition|?
literal|0x01000000
else|:
literal|0x02000000
operator|)
operator|^
operator|(
name|tightEncodingEnabled
condition|?
literal|0x00100000
else|:
literal|0x00200000
operator|)
operator|^
operator|(
name|prefixPacketSize
condition|?
literal|0x00010000
else|:
literal|0x00020000
operator|)
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|OpenWireFormat
name|o
init|=
operator|(
name|OpenWireFormat
operator|)
name|object
decl_stmt|;
return|return
name|o
operator|.
name|stackTraceEnabled
operator|==
name|stackTraceEnabled
operator|&&
name|o
operator|.
name|cacheEnabled
operator|==
name|cacheEnabled
operator|&&
name|o
operator|.
name|version
operator|==
name|version
operator|&&
name|o
operator|.
name|tightEncodingEnabled
operator|==
name|tightEncodingEnabled
operator|&&
name|o
operator|.
name|prefixPacketSize
operator|==
name|prefixPacketSize
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"OpenWireFormat{version="
operator|+
name|version
operator|+
literal|", cacheEnabled="
operator|+
name|cacheEnabled
operator|+
literal|", stackTraceEnabled="
operator|+
name|stackTraceEnabled
operator|+
literal|", tightEncodingEnabled="
operator|+
name|tightEncodingEnabled
operator|+
literal|", prefixPacketSize="
operator|+
name|prefixPacketSize
operator|+
literal|"}"
return|;
block|}
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
specifier|public
name|Packet
name|marshal
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cacheEnabled
condition|)
block|{
name|runMarshallCacheEvictionSweep
argument_list|()
expr_stmt|;
block|}
name|MarshallAware
name|ma
init|=
literal|null
decl_stmt|;
comment|// If not using value caching, then the marshaled form is always the same
if|if
condition|(
operator|!
name|cacheEnabled
operator|&&
operator|(
operator|(
name|DataStructure
operator|)
name|command
operator|)
operator|.
name|isMarshallAware
argument_list|()
condition|)
block|{
name|ma
operator|=
operator|(
name|MarshallAware
operator|)
name|command
expr_stmt|;
block|}
name|ByteSequence
name|sequence
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ma
operator|!=
literal|null
condition|)
block|{
name|sequence
operator|=
name|ma
operator|.
name|getCachedMarshalledForm
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sequence
operator|==
literal|null
condition|)
block|{
name|int
name|size
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|command
operator|!=
literal|null
condition|)
block|{
name|DataStructure
name|c
init|=
operator|(
name|DataStructure
operator|)
name|command
decl_stmt|;
name|byte
name|type
init|=
name|c
operator|.
name|getDataStructureType
argument_list|()
decl_stmt|;
name|DataStreamMarshaller
name|dsm
init|=
operator|(
name|DataStreamMarshaller
operator|)
name|dataMarshallers
index|[
name|type
operator|&
literal|0xFF
index|]
decl_stmt|;
if|if
condition|(
name|dsm
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown data type: "
operator|+
name|type
argument_list|)
throw|;
if|if
condition|(
name|tightEncodingEnabled
condition|)
block|{
name|BooleanStream
name|bs
init|=
operator|new
name|BooleanStream
argument_list|()
decl_stmt|;
name|size
operator|+=
name|dsm
operator|.
name|tightMarshal1
argument_list|(
name|this
argument_list|,
name|c
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|size
operator|+=
name|bs
operator|.
name|marshalledSize
argument_list|()
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|DataOutputStream
name|ds
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixPacketSize
condition|)
block|{
name|ds
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
name|ds
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|bs
operator|.
name|marshal
argument_list|(
name|ds
argument_list|)
expr_stmt|;
name|dsm
operator|.
name|tightMarshal2
argument_list|(
name|this
argument_list|,
name|c
argument_list|,
name|ds
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
name|sequence
operator|=
name|baos
operator|.
name|toByteSequence
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|DataOutputStream
name|ds
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixPacketSize
condition|)
block|{
name|ds
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// we don't know the final size yet but write this here for now.
block|}
name|ds
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|dsm
operator|.
name|looseMarshal
argument_list|(
name|this
argument_list|,
name|c
argument_list|,
name|ds
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
name|sequence
operator|=
name|baos
operator|.
name|toByteSequence
argument_list|()
expr_stmt|;
if|if
condition|(
name|prefixPacketSize
condition|)
block|{
name|size
operator|=
name|sequence
operator|.
name|getLength
argument_list|()
operator|-
literal|4
expr_stmt|;
name|ByteArrayPacket
name|packet
init|=
operator|new
name|ByteArrayPacket
argument_list|(
name|sequence
argument_list|)
decl_stmt|;
name|PacketData
operator|.
name|writeIntBig
argument_list|(
name|packet
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|DataOutputStream
name|daos
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|daos
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|daos
operator|.
name|writeByte
argument_list|(
name|NULL_TYPE
argument_list|)
expr_stmt|;
name|daos
operator|.
name|close
argument_list|()
expr_stmt|;
name|sequence
operator|=
name|baos
operator|.
name|toByteSequence
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ma
operator|!=
literal|null
condition|)
block|{
name|ma
operator|.
name|setCachedMarshalledForm
argument_list|(
name|this
argument_list|,
name|sequence
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ByteArrayPacket
argument_list|(
name|sequence
argument_list|)
return|;
block|}
specifier|public
name|Object
name|unmarshal
parameter_list|(
name|Packet
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteSequence
name|sequence
init|=
name|packet
operator|.
name|asByteSequence
argument_list|()
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|PacketToInputStream
argument_list|(
name|packet
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixPacketSize
condition|)
block|{
name|int
name|size
init|=
name|dis
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|sequence
operator|.
name|getLength
argument_list|()
operator|-
literal|4
operator|!=
name|size
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Packet size does not match marshaled size: "
operator|+
name|size
operator|+
literal|", "
operator|+
operator|(
name|sequence
operator|.
name|getLength
argument_list|()
operator|-
literal|4
operator|)
argument_list|)
expr_stmt|;
comment|//            throw new IOException("Packet size does not match marshaled size");
block|}
name|Object
name|command
init|=
name|doUnmarshal
argument_list|(
name|dis
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|cacheEnabled
operator|&&
operator|(
operator|(
name|DataStructure
operator|)
name|command
operator|)
operator|.
name|isMarshallAware
argument_list|()
condition|)
block|{
operator|(
operator|(
name|MarshallAware
operator|)
name|command
operator|)
operator|.
name|setCachedMarshalledForm
argument_list|(
name|this
argument_list|,
name|sequence
argument_list|)
expr_stmt|;
block|}
return|return
name|command
return|;
block|}
specifier|public
name|void
name|marshal
parameter_list|(
name|Object
name|o
parameter_list|,
name|DataOutputStream
name|ds
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cacheEnabled
condition|)
block|{
name|runMarshallCacheEvictionSweep
argument_list|()
expr_stmt|;
block|}
name|int
name|size
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|DataStructure
name|c
init|=
operator|(
name|DataStructure
operator|)
name|o
decl_stmt|;
name|byte
name|type
init|=
name|c
operator|.
name|getDataStructureType
argument_list|()
decl_stmt|;
name|DataStreamMarshaller
name|dsm
init|=
operator|(
name|DataStreamMarshaller
operator|)
name|dataMarshallers
index|[
name|type
operator|&
literal|0xFF
index|]
decl_stmt|;
if|if
condition|(
name|dsm
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown data type: "
operator|+
name|type
argument_list|)
throw|;
name|BooleanStream
name|bs
init|=
operator|new
name|BooleanStream
argument_list|()
decl_stmt|;
name|size
operator|+=
name|dsm
operator|.
name|tightMarshal1
argument_list|(
name|this
argument_list|,
name|c
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|size
operator|+=
name|bs
operator|.
name|marshalledSize
argument_list|()
expr_stmt|;
name|ds
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|ds
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|bs
operator|.
name|marshal
argument_list|(
name|ds
argument_list|)
expr_stmt|;
name|dsm
operator|.
name|tightMarshal2
argument_list|(
name|this
argument_list|,
name|c
argument_list|,
name|ds
argument_list|,
name|bs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ds
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|ds
operator|.
name|writeByte
argument_list|(
name|NULL_TYPE
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Object
name|unmarshal
parameter_list|(
name|DataInputStream
name|dis
parameter_list|)
throws|throws
name|IOException
block|{
name|dis
operator|.
name|readInt
argument_list|()
expr_stmt|;
return|return
name|doUnmarshal
argument_list|(
name|dis
argument_list|)
return|;
block|}
comment|/**      * Allows you to dynamically switch the version of the openwire protocol being used.      * @param version      */
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|String
name|mfName
init|=
literal|"org.apache.activemq.openwire.v"
operator|+
name|version
operator|+
literal|".MarshallerFactory"
decl_stmt|;
name|Class
name|mfClass
decl_stmt|;
try|try
block|{
name|mfClass
operator|=
name|ClassLoading
operator|.
name|loadClass
argument_list|(
name|mfName
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IllegalArgumentException
operator|)
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid version: "
operator|+
name|version
operator|+
literal|", could not load "
operator|+
name|mfName
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|Method
name|method
init|=
name|mfClass
operator|.
name|getMethod
argument_list|(
literal|"createMarshallerMap"
argument_list|,
operator|new
name|Class
index|[]
block|{
name|OpenWireFormat
operator|.
name|class
block|}
argument_list|)
decl_stmt|;
name|dataMarshallers
operator|=
operator|(
name|DataStreamMarshaller
index|[]
operator|)
name|method
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|new
name|Object
index|[]
block|{
name|this
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|IllegalArgumentException
operator|)
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid version: "
operator|+
name|version
operator|+
literal|", "
operator|+
name|mfName
operator|+
literal|" does not properly implement the createMarshallerMap method."
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
specifier|public
name|Object
name|doUnmarshal
parameter_list|(
name|DataInputStream
name|dis
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|dataType
init|=
name|dis
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|dataType
operator|!=
name|NULL_TYPE
condition|)
block|{
name|DataStreamMarshaller
name|dsm
init|=
operator|(
name|DataStreamMarshaller
operator|)
name|dataMarshallers
index|[
name|dataType
operator|&
literal|0xFF
index|]
decl_stmt|;
if|if
condition|(
name|dsm
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown data type: "
operator|+
name|dataType
argument_list|)
throw|;
name|Object
name|data
init|=
name|dsm
operator|.
name|createObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|tightEncodingEnabled
condition|)
block|{
name|BooleanStream
name|bs
init|=
operator|new
name|BooleanStream
argument_list|()
decl_stmt|;
name|bs
operator|.
name|unmarshal
argument_list|(
name|dis
argument_list|)
expr_stmt|;
name|dsm
operator|.
name|tightUnmarshal
argument_list|(
name|this
argument_list|,
name|data
argument_list|,
name|dis
argument_list|,
name|bs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dsm
operator|.
name|looseUnmarshal
argument_list|(
name|this
argument_list|,
name|data
argument_list|,
name|dis
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|int
name|tightMarshalNestedObject1
parameter_list|(
name|DataStructure
name|o
parameter_list|,
name|BooleanStream
name|bs
parameter_list|)
throws|throws
name|IOException
block|{
name|bs
operator|.
name|writeBoolean
argument_list|(
name|o
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|o
operator|.
name|isMarshallAware
argument_list|()
condition|)
block|{
name|MarshallAware
name|ma
init|=
operator|(
name|MarshallAware
operator|)
name|o
decl_stmt|;
name|ByteSequence
name|sequence
init|=
name|ma
operator|.
name|getCachedMarshalledForm
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|bs
operator|.
name|writeBoolean
argument_list|(
name|sequence
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequence
operator|!=
literal|null
condition|)
block|{
return|return
literal|1
operator|+
name|sequence
operator|.
name|getLength
argument_list|()
return|;
block|}
block|}
name|byte
name|type
init|=
name|o
operator|.
name|getDataStructureType
argument_list|()
decl_stmt|;
name|DataStreamMarshaller
name|dsm
init|=
operator|(
name|DataStreamMarshaller
operator|)
name|dataMarshallers
index|[
name|type
operator|&
literal|0xFF
index|]
decl_stmt|;
if|if
condition|(
name|dsm
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown data type: "
operator|+
name|type
argument_list|)
throw|;
return|return
literal|1
operator|+
name|dsm
operator|.
name|tightMarshal1
argument_list|(
name|this
argument_list|,
name|o
argument_list|,
name|bs
argument_list|)
return|;
block|}
specifier|public
name|void
name|tightMarshalNestedObject2
parameter_list|(
name|DataStructure
name|o
parameter_list|,
name|DataOutputStream
name|ds
parameter_list|,
name|BooleanStream
name|bs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|bs
operator|.
name|readBoolean
argument_list|()
condition|)
return|return;
name|byte
name|type
init|=
name|o
operator|.
name|getDataStructureType
argument_list|()
decl_stmt|;
name|ds
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|.
name|isMarshallAware
argument_list|()
operator|&&
name|bs
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|MarshallAware
name|ma
init|=
operator|(
name|MarshallAware
operator|)
name|o
decl_stmt|;
name|ByteSequence
name|sequence
init|=
name|ma
operator|.
name|getCachedMarshalledForm
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|ds
operator|.
name|write
argument_list|(
name|sequence
operator|.
name|getData
argument_list|()
argument_list|,
name|sequence
operator|.
name|getOffset
argument_list|()
argument_list|,
name|sequence
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DataStreamMarshaller
name|dsm
init|=
operator|(
name|DataStreamMarshaller
operator|)
name|dataMarshallers
index|[
name|type
operator|&
literal|0xFF
index|]
decl_stmt|;
if|if
condition|(
name|dsm
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown data type: "
operator|+
name|type
argument_list|)
throw|;
name|dsm
operator|.
name|tightMarshal2
argument_list|(
name|this
argument_list|,
name|o
argument_list|,
name|ds
argument_list|,
name|bs
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|DataStructure
name|tightUnmarshalNestedObject
parameter_list|(
name|DataInputStream
name|dis
parameter_list|,
name|BooleanStream
name|bs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bs
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|byte
name|dataType
init|=
name|dis
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|DataStreamMarshaller
name|dsm
init|=
operator|(
name|DataStreamMarshaller
operator|)
name|dataMarshallers
index|[
name|dataType
operator|&
literal|0xFF
index|]
decl_stmt|;
if|if
condition|(
name|dsm
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown data type: "
operator|+
name|dataType
argument_list|)
throw|;
name|DataStructure
name|data
init|=
name|dsm
operator|.
name|createObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|isMarshallAware
argument_list|()
operator|&&
name|bs
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|dis
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|dis
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|BooleanStream
name|bs2
init|=
operator|new
name|BooleanStream
argument_list|()
decl_stmt|;
name|bs2
operator|.
name|unmarshal
argument_list|(
name|dis
argument_list|)
expr_stmt|;
name|dsm
operator|.
name|tightUnmarshal
argument_list|(
name|this
argument_list|,
name|data
argument_list|,
name|dis
argument_list|,
name|bs2
argument_list|)
expr_stmt|;
comment|// TODO: extract the sequence from the dis and associate it.
comment|//                MarshallAware ma = (MarshallAware)data
comment|//                ma.setCachedMarshalledForm(this, sequence);
block|}
else|else
block|{
name|dsm
operator|.
name|tightUnmarshal
argument_list|(
name|this
argument_list|,
name|data
argument_list|,
name|dis
argument_list|,
name|bs
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|DataStructure
name|looseUnmarshalNestedObject
parameter_list|(
name|DataInputStream
name|dis
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dis
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|byte
name|dataType
init|=
name|dis
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|DataStreamMarshaller
name|dsm
init|=
operator|(
name|DataStreamMarshaller
operator|)
name|dataMarshallers
index|[
name|dataType
operator|&
literal|0xFF
index|]
decl_stmt|;
if|if
condition|(
name|dsm
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown data type: "
operator|+
name|dataType
argument_list|)
throw|;
name|DataStructure
name|data
init|=
name|dsm
operator|.
name|createObject
argument_list|()
decl_stmt|;
name|dsm
operator|.
name|looseUnmarshal
argument_list|(
name|this
argument_list|,
name|data
argument_list|,
name|dis
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|void
name|looseMarshalNestedObject
parameter_list|(
name|DataStructure
name|o
parameter_list|,
name|DataOutputStream
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|writeBoolean
argument_list|(
name|o
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|byte
name|type
init|=
name|o
operator|.
name|getDataStructureType
argument_list|()
decl_stmt|;
name|dataOut
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|DataStreamMarshaller
name|dsm
init|=
operator|(
name|DataStreamMarshaller
operator|)
name|dataMarshallers
index|[
name|type
operator|&
literal|0xFF
index|]
decl_stmt|;
if|if
condition|(
name|dsm
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown data type: "
operator|+
name|type
argument_list|)
throw|;
name|dsm
operator|.
name|looseMarshal
argument_list|(
name|this
argument_list|,
name|o
argument_list|,
name|dataOut
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|runMarshallCacheEvictionSweep
parameter_list|()
block|{
comment|// Do we need to start evicting??
while|while
condition|(
name|marshallCacheMap
operator|.
name|size
argument_list|()
operator|>
name|MARSHAL_CACHE_PREFERED_SIZE
condition|)
block|{
name|marshallCacheMap
operator|.
name|remove
argument_list|(
name|marshallCache
index|[
name|nextMarshallCacheEvictionIndex
index|]
argument_list|)
expr_stmt|;
name|marshallCache
index|[
name|nextMarshallCacheEvictionIndex
index|]
operator|=
literal|null
expr_stmt|;
name|nextMarshallCacheEvictionIndex
operator|++
expr_stmt|;
if|if
condition|(
name|nextMarshallCacheEvictionIndex
operator|>=
name|MARSHAL_CACHE_SIZE
condition|)
block|{
name|nextMarshallCacheEvictionIndex
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Short
name|getMarshallCacheIndex
parameter_list|(
name|DataStructure
name|o
parameter_list|)
block|{
return|return
operator|(
name|Short
operator|)
name|marshallCacheMap
operator|.
name|get
argument_list|(
name|o
argument_list|)
return|;
block|}
specifier|public
name|Short
name|addToMarshallCache
parameter_list|(
name|DataStructure
name|o
parameter_list|)
block|{
name|short
name|i
init|=
name|nextMarshallCacheIndex
operator|++
decl_stmt|;
if|if
condition|(
name|nextMarshallCacheIndex
operator|>=
name|MARSHAL_CACHE_SIZE
condition|)
block|{
name|nextMarshallCacheIndex
operator|=
literal|0
expr_stmt|;
block|}
comment|// We can only cache that item if there is space left.
if|if
condition|(
name|marshallCacheMap
operator|.
name|size
argument_list|()
operator|<
name|MARSHAL_CACHE_SIZE
condition|)
block|{
name|marshallCache
index|[
name|i
index|]
operator|=
name|o
expr_stmt|;
name|Short
name|index
init|=
operator|new
name|Short
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|marshallCacheMap
operator|.
name|put
argument_list|(
name|o
argument_list|,
name|index
argument_list|)
expr_stmt|;
return|return
name|index
return|;
block|}
else|else
block|{
comment|// Use -1 to indicate that the value was not cached due to cache being full.
return|return
operator|new
name|Short
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|setInUnmarshallCache
parameter_list|(
name|short
name|index
parameter_list|,
name|DataStructure
name|o
parameter_list|)
block|{
comment|// There was no space left in the cache, so we can't
comment|// put this in the cache.
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
return|return;
name|unmarshallCache
index|[
name|index
index|]
operator|=
name|o
expr_stmt|;
block|}
specifier|public
name|DataStructure
name|getFromUnmarshallCache
parameter_list|(
name|short
name|index
parameter_list|)
block|{
return|return
name|unmarshallCache
index|[
name|index
index|]
return|;
block|}
specifier|public
name|void
name|setStackTraceEnabled
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
name|stackTraceEnabled
operator|=
name|b
expr_stmt|;
block|}
specifier|public
name|boolean
name|isStackTraceEnabled
parameter_list|()
block|{
return|return
name|stackTraceEnabled
return|;
block|}
specifier|public
name|boolean
name|isTcpNoDelayEnabled
parameter_list|()
block|{
return|return
name|tcpNoDelayEnabled
return|;
block|}
specifier|public
name|void
name|setTcpNoDelayEnabled
parameter_list|(
name|boolean
name|tcpNoDelayEnabled
parameter_list|)
block|{
name|this
operator|.
name|tcpNoDelayEnabled
operator|=
name|tcpNoDelayEnabled
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCacheEnabled
parameter_list|()
block|{
return|return
name|cacheEnabled
return|;
block|}
specifier|public
name|void
name|setCacheEnabled
parameter_list|(
name|boolean
name|cacheEnabled
parameter_list|)
block|{
name|this
operator|.
name|cacheEnabled
operator|=
name|cacheEnabled
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTightEncodingEnabled
parameter_list|()
block|{
return|return
name|tightEncodingEnabled
return|;
block|}
specifier|public
name|void
name|setTightEncodingEnabled
parameter_list|(
name|boolean
name|tightEncodingEnabled
parameter_list|)
block|{
name|this
operator|.
name|tightEncodingEnabled
operator|=
name|tightEncodingEnabled
expr_stmt|;
block|}
specifier|public
name|boolean
name|isPrefixPacketSize
parameter_list|()
block|{
return|return
name|prefixPacketSize
return|;
block|}
specifier|public
name|void
name|setPrefixPacketSize
parameter_list|(
name|boolean
name|prefixPacketSize
parameter_list|)
block|{
name|this
operator|.
name|prefixPacketSize
operator|=
name|prefixPacketSize
expr_stmt|;
block|}
block|}
end_class

end_unit

