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
name|DataInputStream
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
name|util
operator|.
name|zip
operator|.
name|CRC32
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|PacketOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|apache
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
name|activeio
operator|.
name|packet
operator|.
name|Packet
import|;
end_import

begin_comment
comment|/**  * Serializes/Deserializes data records.   *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|Record
block|{
specifier|static
specifier|final
specifier|public
name|int
name|RECORD_HEADER_SIZE
init|=
literal|8
operator|+
name|Location
operator|.
name|SERIALIZED_SIZE
decl_stmt|;
specifier|static
specifier|final
specifier|public
name|int
name|RECORD_FOOTER_SIZE
init|=
literal|12
operator|+
name|Location
operator|.
name|SERIALIZED_SIZE
decl_stmt|;
specifier|static
specifier|final
specifier|public
name|int
name|RECORD_BASE_SIZE
init|=
name|RECORD_HEADER_SIZE
operator|+
name|RECORD_FOOTER_SIZE
decl_stmt|;
specifier|static
specifier|final
specifier|public
name|byte
index|[]
name|START_OF_RECORD
init|=
operator|new
name|byte
index|[]
block|{
literal|'S'
block|,
literal|'o'
block|,
literal|'R'
block|}
decl_stmt|;
specifier|static
specifier|final
specifier|public
name|byte
index|[]
name|END_OF_RECORD
init|=
operator|new
name|byte
index|[]
block|{
literal|'E'
block|,
literal|'o'
block|,
literal|'R'
block|,
literal|'.'
block|}
decl_stmt|;
specifier|static
specifier|final
specifier|public
name|int
name|SELECTED_CHECKSUM_ALGORITHIM
decl_stmt|;
specifier|static
specifier|final
specifier|public
name|int
name|NO_CHECKSUM_ALGORITHIM
init|=
literal|0
decl_stmt|;
specifier|static
specifier|final
specifier|public
name|int
name|HASH_CHECKSUM_ALGORITHIM
init|=
literal|1
decl_stmt|;
specifier|static
specifier|final
specifier|public
name|int
name|CRC32_CHECKSUM_ALGORITHIM
init|=
literal|2
decl_stmt|;
static|static
block|{
name|String
name|type
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.activeio.journal.active.SELECTED_CHECKSUM_ALGORITHIM"
argument_list|,
literal|"none"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|SELECTED_CHECKSUM_ALGORITHIM
operator|=
name|NO_CHECKSUM_ALGORITHIM
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"crc32"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|SELECTED_CHECKSUM_ALGORITHIM
operator|=
name|CRC32_CHECKSUM_ALGORITHIM
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"hash"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|SELECTED_CHECKSUM_ALGORITHIM
operator|=
name|HASH_CHECKSUM_ALGORITHIM
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"System property 'org.apache.activeio.journal.active.SELECTED_CHECKSUM_ALGORITHIM' not set properly.  Valid values are: 'none', 'hash', or 'crc32'"
argument_list|)
expr_stmt|;
name|SELECTED_CHECKSUM_ALGORITHIM
operator|=
name|NO_CHECKSUM_ALGORITHIM
expr_stmt|;
block|}
block|}
specifier|static
specifier|public
name|boolean
name|isChecksumingEnabled
parameter_list|()
block|{
return|return
name|SELECTED_CHECKSUM_ALGORITHIM
operator|!=
name|NO_CHECKSUM_ALGORITHIM
return|;
block|}
specifier|private
specifier|final
name|ByteArrayPacket
name|headerFooterPacket
init|=
operator|new
name|ByteArrayPacket
argument_list|(
operator|new
name|byte
index|[
name|RECORD_BASE_SIZE
index|]
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DataOutputStream
name|headerFooterData
init|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|PacketOutputStream
argument_list|(
name|headerFooterPacket
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|int
name|payloadLength
decl_stmt|;
specifier|private
name|Location
name|location
decl_stmt|;
specifier|private
name|byte
name|recordType
decl_stmt|;
specifier|private
name|long
name|checksum
decl_stmt|;
specifier|private
name|Location
name|mark
decl_stmt|;
specifier|private
name|Packet
name|payload
decl_stmt|;
specifier|public
name|Record
parameter_list|()
block|{             }
specifier|public
name|Record
parameter_list|(
name|byte
name|recordType
parameter_list|,
name|Packet
name|payload
parameter_list|,
name|Location
name|mark
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|recordType
argument_list|,
name|payload
argument_list|,
name|mark
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Record
parameter_list|(
name|Location
name|location
parameter_list|,
name|byte
name|recordType
parameter_list|,
name|Packet
name|payload
parameter_list|,
name|Location
name|mark
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
name|this
operator|.
name|recordType
operator|=
name|recordType
expr_stmt|;
name|this
operator|.
name|mark
operator|=
name|mark
expr_stmt|;
name|this
operator|.
name|payload
operator|=
name|payload
operator|.
name|slice
argument_list|()
expr_stmt|;
name|this
operator|.
name|payloadLength
operator|=
name|payload
operator|.
name|remaining
argument_list|()
expr_stmt|;
if|if
condition|(
name|isChecksumingEnabled
argument_list|()
condition|)
block|{
name|checksum
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|PacketToInputStream
argument_list|(
name|this
operator|.
name|payload
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writeHeader
argument_list|(
name|headerFooterData
argument_list|)
expr_stmt|;
name|writeFooter
argument_list|(
name|headerFooterData
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setLocation
parameter_list|(
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
name|headerFooterPacket
operator|.
name|clear
argument_list|()
expr_stmt|;
name|headerFooterPacket
operator|.
name|position
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|location
operator|.
name|writeToDataOutput
argument_list|(
name|headerFooterData
argument_list|)
expr_stmt|;
name|headerFooterPacket
operator|.
name|position
argument_list|(
name|RECORD_HEADER_SIZE
operator|+
literal|8
argument_list|)
expr_stmt|;
name|location
operator|.
name|writeToDataOutput
argument_list|(
name|headerFooterData
argument_list|)
expr_stmt|;
name|payload
operator|.
name|clear
argument_list|()
expr_stmt|;
name|headerFooterPacket
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|headerFooterPacket
operator|.
name|limit
argument_list|(
name|RECORD_HEADER_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeHeader
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|START_OF_RECORD
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|recordType
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|location
operator|!=
literal|null
condition|)
name|location
operator|.
name|writeToDataOutput
argument_list|(
name|out
argument_list|)
expr_stmt|;
else|else
name|out
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|readHeader
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|readAndCheckConstant
argument_list|(
name|in
argument_list|,
name|START_OF_RECORD
argument_list|,
literal|"Invalid record header: start of record constant missing."
argument_list|)
expr_stmt|;
name|recordType
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|payloadLength
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|payloadLength
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid record header: record length cannot be less than zero."
argument_list|)
throw|;
name|location
operator|=
name|Location
operator|.
name|readFromDataInput
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeFooter
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|checksum
argument_list|)
expr_stmt|;
if|if
condition|(
name|location
operator|!=
literal|null
condition|)
name|location
operator|.
name|writeToDataOutput
argument_list|(
name|out
argument_list|)
expr_stmt|;
else|else
name|out
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|END_OF_RECORD
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|readFooter
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|l
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|isChecksumingEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|l
operator|!=
name|checksum
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid record footer: checksum does not match."
argument_list|)
throw|;
block|}
else|else
block|{
name|checksum
operator|=
name|l
expr_stmt|;
block|}
name|Location
name|loc
init|=
name|Location
operator|.
name|readFromDataInput
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|loc
operator|.
name|equals
argument_list|(
name|location
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid record footer: location id does not match."
argument_list|)
throw|;
name|readAndCheckConstant
argument_list|(
name|in
argument_list|,
name|END_OF_RECORD
argument_list|,
literal|"Invalid record header: end of record constant missing."
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param randomAccessFile      * @throws IOException      */
specifier|public
name|void
name|checksum
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|SELECTED_CHECKSUM_ALGORITHIM
operator|==
name|HASH_CHECKSUM_ALGORITHIM
condition|)
block|{
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|byte
name|rc
index|[]
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|payloadLength
condition|;
control|)
block|{
name|int
name|l
init|=
name|Math
operator|.
name|min
argument_list|(
name|buffer
operator|.
name|length
argument_list|,
name|payloadLength
operator|-
name|i
argument_list|)
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|l
condition|;
name|j
operator|++
control|)
block|{
name|rc
index|[
name|j
operator|%
literal|8
index|]
operator|^=
name|buffer
index|[
name|j
index|]
expr_stmt|;
block|}
name|i
operator|+=
name|l
expr_stmt|;
block|}
name|checksum
operator|=
operator|(
name|rc
index|[
literal|0
index|]
operator|)
operator||
operator|(
name|rc
index|[
literal|1
index|]
operator|<<
literal|1
operator|)
operator||
operator|(
name|rc
index|[
literal|2
index|]
operator|<<
literal|2
operator|)
operator||
operator|(
name|rc
index|[
literal|3
index|]
operator|<<
literal|3
operator|)
operator||
operator|(
name|rc
index|[
literal|4
index|]
operator|<<
literal|4
operator|)
operator||
operator|(
name|rc
index|[
literal|5
index|]
operator|<<
literal|5
operator|)
operator||
operator|(
name|rc
index|[
literal|6
index|]
operator|<<
literal|6
operator|)
operator||
operator|(
name|rc
index|[
literal|7
index|]
operator|<<
literal|7
operator|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SELECTED_CHECKSUM_ALGORITHIM
operator|==
name|CRC32_CHECKSUM_ALGORITHIM
condition|)
block|{
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|CRC32
name|crc32
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|payloadLength
condition|;
control|)
block|{
name|int
name|l
init|=
name|Math
operator|.
name|min
argument_list|(
name|buffer
operator|.
name|length
argument_list|,
name|payloadLength
operator|-
name|i
argument_list|)
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|crc32
operator|.
name|update
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|i
operator|+=
name|l
expr_stmt|;
block|}
name|checksum
operator|=
name|crc32
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|checksum
operator|=
literal|0L
expr_stmt|;
block|}
block|}
comment|/**      */
specifier|private
name|void
name|readAndCheckConstant
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|byte
index|[]
name|byteConstant
parameter_list|,
name|String
name|errorMessage
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|byteConstant
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|checkByte
init|=
name|byteConstant
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|readByte
argument_list|()
operator|!=
name|checkByte
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|errorMessage
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|boolean
name|readFromPacket
parameter_list|(
name|Packet
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|Packet
name|dup
init|=
name|packet
operator|.
name|duplicate
argument_list|()
decl_stmt|;
if|if
condition|(
name|dup
operator|.
name|remaining
argument_list|()
operator|<
name|RECORD_HEADER_SIZE
condition|)
return|return
literal|false
return|;
name|DataInputStream
name|is
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|PacketToInputStream
argument_list|(
name|dup
argument_list|)
argument_list|)
decl_stmt|;
name|readHeader
argument_list|(
name|is
argument_list|)
expr_stmt|;
if|if
condition|(
name|dup
operator|.
name|remaining
argument_list|()
operator|<
name|payloadLength
operator|+
name|RECORD_FOOTER_SIZE
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Set limit to create a slice of the payload.
name|dup
operator|.
name|limit
argument_list|(
name|dup
operator|.
name|position
argument_list|()
operator|+
name|payloadLength
argument_list|)
expr_stmt|;
name|this
operator|.
name|payload
operator|=
name|dup
operator|.
name|slice
argument_list|()
expr_stmt|;
if|if
condition|(
name|isChecksumingEnabled
argument_list|()
condition|)
block|{
name|checksum
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|PacketToInputStream
argument_list|(
name|payload
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// restore the limit and seek to the footer.
name|dup
operator|.
name|limit
argument_list|(
name|packet
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
name|dup
operator|.
name|position
argument_list|(
name|dup
operator|.
name|position
argument_list|()
operator|+
name|payloadLength
argument_list|)
expr_stmt|;
name|readFooter
argument_list|(
name|is
argument_list|)
expr_stmt|;
comment|// If every thing went well.. advance the position of the orignal packet.
name|packet
operator|.
name|position
argument_list|(
name|dup
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|dup
operator|.
name|dispose
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * @return Returns the checksum.      */
specifier|public
name|long
name|getChecksum
parameter_list|()
block|{
return|return
name|checksum
return|;
block|}
comment|/**      * @return Returns the length.      */
specifier|public
name|int
name|getPayloadLength
parameter_list|()
block|{
return|return
name|payloadLength
return|;
block|}
comment|/**      * @return Returns the length of the record .      */
specifier|public
name|int
name|getRecordLength
parameter_list|()
block|{
return|return
name|payloadLength
operator|+
name|Record
operator|.
name|RECORD_BASE_SIZE
return|;
block|}
comment|/**      * @return Returns the location.      */
specifier|public
name|Location
name|getLocation
parameter_list|()
block|{
return|return
name|location
return|;
block|}
comment|/**      * @return Returns the mark.      */
specifier|public
name|Location
name|getMark
parameter_list|()
block|{
return|return
name|mark
return|;
block|}
comment|/**      * @return Returns the payload.      */
specifier|public
name|Packet
name|getPayload
parameter_list|()
block|{
return|return
name|payload
return|;
block|}
comment|/**      * @return Returns the recordType.      */
specifier|public
name|byte
name|getRecordType
parameter_list|()
block|{
return|return
name|recordType
return|;
block|}
specifier|public
name|boolean
name|hasRemaining
parameter_list|()
block|{
return|return
name|headerFooterPacket
operator|.
name|position
argument_list|()
operator|!=
name|RECORD_BASE_SIZE
return|;
block|}
specifier|public
name|void
name|read
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
comment|// push the header
name|headerFooterPacket
operator|.
name|read
argument_list|(
name|packet
argument_list|)
expr_stmt|;
comment|// push the payload.
name|payload
operator|.
name|read
argument_list|(
name|packet
argument_list|)
expr_stmt|;
comment|// Can we switch to the footer now?
if|if
condition|(
operator|!
name|payload
operator|.
name|hasRemaining
argument_list|()
operator|&&
name|headerFooterPacket
operator|.
name|position
argument_list|()
operator|==
name|RECORD_HEADER_SIZE
condition|)
block|{
name|headerFooterPacket
operator|.
name|position
argument_list|(
name|RECORD_HEADER_SIZE
argument_list|)
expr_stmt|;
name|headerFooterPacket
operator|.
name|limit
argument_list|(
name|RECORD_BASE_SIZE
argument_list|)
expr_stmt|;
name|headerFooterPacket
operator|.
name|read
argument_list|(
name|packet
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|payload
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|payload
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

