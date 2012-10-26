begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  Licensed to the Apache Software Foundation (ASF) under one or more  *  contributor license agreements.  See the NOTICE file distributed with  *  this work for additional information regarding copyright ownership.  *  The ASF licenses this file to You under the Apache License, Version 2.0  *  (the "License"); you may not use this file except in compliance with  *  the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  *  */
end_comment

begin_comment
comment|/*  * This package is based on the work done by Timothy Gerard Endres  * (time@ice.com) to whom the Ant project is very grateful for his great code.  */
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
name|tar
package|;
end_package

begin_comment
comment|/**  * This interface contains all the definitions used in the package.  *  */
end_comment

begin_comment
comment|// CheckStyle:InterfaceIsTypeCheck OFF (bc)
end_comment

begin_interface
specifier|public
interface|interface
name|TarConstants
block|{
comment|/**      * The length of the name field in a header buffer.      */
name|int
name|NAMELEN
init|=
literal|100
decl_stmt|;
comment|/**      * The length of the mode field in a header buffer.      */
name|int
name|MODELEN
init|=
literal|8
decl_stmt|;
comment|/**      * The length of the user id field in a header buffer.      */
name|int
name|UIDLEN
init|=
literal|8
decl_stmt|;
comment|/**      * The length of the group id field in a header buffer.      */
name|int
name|GIDLEN
init|=
literal|8
decl_stmt|;
comment|/**      * The length of the checksum field in a header buffer.      */
name|int
name|CHKSUMLEN
init|=
literal|8
decl_stmt|;
comment|/**      * The length of the size field in a header buffer.      */
name|int
name|SIZELEN
init|=
literal|12
decl_stmt|;
comment|/**      * The maximum size of a file in a tar archive (That's 11 sevens, octal).      */
name|long
name|MAXSIZE
init|=
literal|077777777777L
decl_stmt|;
comment|/**      * The length of the magic field in a header buffer.      */
name|int
name|MAGICLEN
init|=
literal|8
decl_stmt|;
comment|/**      * The length of the modification time field in a header buffer.      */
name|int
name|MODTIMELEN
init|=
literal|12
decl_stmt|;
comment|/**      * The length of the user name field in a header buffer.      */
name|int
name|UNAMELEN
init|=
literal|32
decl_stmt|;
comment|/**      * The length of the group name field in a header buffer.      */
name|int
name|GNAMELEN
init|=
literal|32
decl_stmt|;
comment|/**      * The length of the devices field in a header buffer.      */
name|int
name|DEVLEN
init|=
literal|8
decl_stmt|;
comment|/**      * LF_ constants represent the "link flag" of an entry, or more commonly,      * the "entry type". This is the "old way" of indicating a normal file.      */
name|byte
name|LF_OLDNORM
init|=
literal|0
decl_stmt|;
comment|/**      * Normal file type.      */
name|byte
name|LF_NORMAL
init|=
operator|(
name|byte
operator|)
literal|'0'
decl_stmt|;
comment|/**      * Link file type.      */
name|byte
name|LF_LINK
init|=
operator|(
name|byte
operator|)
literal|'1'
decl_stmt|;
comment|/**      * Symbolic link file type.      */
name|byte
name|LF_SYMLINK
init|=
operator|(
name|byte
operator|)
literal|'2'
decl_stmt|;
comment|/**      * Character device file type.      */
name|byte
name|LF_CHR
init|=
operator|(
name|byte
operator|)
literal|'3'
decl_stmt|;
comment|/**      * Block device file type.      */
name|byte
name|LF_BLK
init|=
operator|(
name|byte
operator|)
literal|'4'
decl_stmt|;
comment|/**      * Directory file type.      */
name|byte
name|LF_DIR
init|=
operator|(
name|byte
operator|)
literal|'5'
decl_stmt|;
comment|/**      * FIFO (pipe) file type.      */
name|byte
name|LF_FIFO
init|=
operator|(
name|byte
operator|)
literal|'6'
decl_stmt|;
comment|/**      * Contiguous file type.      */
name|byte
name|LF_CONTIG
init|=
operator|(
name|byte
operator|)
literal|'7'
decl_stmt|;
comment|/**      * The magic tag representing a POSIX tar archive.      */
name|String
name|TMAGIC
init|=
literal|"ustar"
decl_stmt|;
comment|/**      * The magic tag representing a GNU tar archive.      */
name|String
name|GNU_TMAGIC
init|=
literal|"ustar  "
decl_stmt|;
comment|/**      * The namr of the GNU tar entry which contains a long name.      */
name|String
name|GNU_LONGLINK
init|=
literal|"././@LongLink"
decl_stmt|;
comment|/**      * Identifies the *next* file on the tape as having a long name.      */
name|byte
name|LF_GNUTYPE_LONGNAME
init|=
operator|(
name|byte
operator|)
literal|'L'
decl_stmt|;
block|}
end_interface

end_unit

