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
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * This class represents an entry in a Tar archive. It consists  * of the entry's header, as well as the entry's File. Entries  * can be instantiated in one of three ways, depending on how  * they are to be used.  *<p>  * TarEntries that are created from the header bytes read from  * an archive are instantiated with the TarEntry( byte[] )  * constructor. These entries will be used when extracting from  * or listing the contents of an archive. These entries have their  * header filled in using the header bytes. They also set the File  * to null, since they reference an archive entry not a file.  *<p>  * TarEntries that are created from Files that are to be written  * into an archive are instantiated with the TarEntry( File )  * constructor. These entries have their header filled in using  * the File's information. They also keep a reference to the File  * for convenience when writing entries.  *<p>  * Finally, TarEntries can be constructed from nothing but a name.  * This allows the programmer to construct the entry by hand, for  * instance when only an InputStream is available for writing to  * the archive, and the header information is constructed from  * other information. In this case the header fields are set to  * defaults and the File is set to null.  *  *<p>  * The C structure for a Tar Entry's header is:  *<pre>  * struct header {  * char name[NAMSIZ];  * char mode[8];  * char uid[8];  * char gid[8];  * char size[12];  * char mtime[12];  * char chksum[8];  * char linkflag;  * char linkname[NAMSIZ];  * char magic[8];  * char uname[TUNMLEN];  * char gname[TGNMLEN];  * char devmajor[8];  * char devminor[8];  * } header;  *</pre>  *  */
end_comment

begin_class
specifier|public
class|class
name|TarEntry
implements|implements
name|TarConstants
block|{
comment|/** The entry's name. */
specifier|private
name|StringBuffer
name|name
decl_stmt|;
comment|/** The entry's permission mode. */
specifier|private
name|int
name|mode
decl_stmt|;
comment|/** The entry's user id. */
specifier|private
name|int
name|userId
decl_stmt|;
comment|/** The entry's group id. */
specifier|private
name|int
name|groupId
decl_stmt|;
comment|/** The entry's size. */
specifier|private
name|long
name|size
decl_stmt|;
comment|/** The entry's modification time. */
specifier|private
name|long
name|modTime
decl_stmt|;
comment|/** The entry's link flag. */
specifier|private
name|byte
name|linkFlag
decl_stmt|;
comment|/** The entry's link name. */
specifier|private
name|StringBuffer
name|linkName
decl_stmt|;
comment|/** The entry's magic tag. */
specifier|private
name|StringBuffer
name|magic
decl_stmt|;
comment|/** The entry's user name. */
specifier|private
name|StringBuffer
name|userName
decl_stmt|;
comment|/** The entry's group name. */
specifier|private
name|StringBuffer
name|groupName
decl_stmt|;
comment|/** The entry's major device number. */
specifier|private
name|int
name|devMajor
decl_stmt|;
comment|/** The entry's minor device number. */
specifier|private
name|int
name|devMinor
decl_stmt|;
comment|/** The entry's file reference */
specifier|private
name|File
name|file
decl_stmt|;
comment|/** Maximum length of a user's name in the tar file */
specifier|public
specifier|static
specifier|final
name|int
name|MAX_NAMELEN
init|=
literal|31
decl_stmt|;
comment|/** Default permissions bits for directories */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_DIR_MODE
init|=
literal|040755
decl_stmt|;
comment|/** Default permissions bits for files */
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_FILE_MODE
init|=
literal|0100644
decl_stmt|;
comment|/** Convert millis to seconds */
specifier|public
specifier|static
specifier|final
name|int
name|MILLIS_PER_SECOND
init|=
literal|1000
decl_stmt|;
comment|/**      * Construct an empty entry and prepares the header values.      */
specifier|private
name|TarEntry
parameter_list|()
block|{
name|this
operator|.
name|magic
operator|=
operator|new
name|StringBuffer
argument_list|(
name|TMAGIC
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|this
operator|.
name|linkName
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|String
name|user
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|.
name|length
argument_list|()
operator|>
name|MAX_NAMELEN
condition|)
block|{
name|user
operator|=
name|user
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|MAX_NAMELEN
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|userId
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|groupId
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|userName
operator|=
operator|new
name|StringBuffer
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupName
operator|=
operator|new
name|StringBuffer
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Construct an entry with only a name. This allows the programmer      * to construct the entry's header "by hand". File is set to null.      *      * @param name the entry name      */
specifier|public
name|TarEntry
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct an entry with only a name. This allows the programmer      * to construct the entry's header "by hand". File is set to null.      *      * @param name the entry name      * @param preserveLeadingSlashes whether to allow leading slashes      * in the name.      */
specifier|public
name|TarEntry
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|preserveLeadingSlashes
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|name
operator|=
name|normalizeFileName
argument_list|(
name|name
argument_list|,
name|preserveLeadingSlashes
argument_list|)
expr_stmt|;
name|boolean
name|isDir
init|=
name|name
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|this
operator|.
name|devMajor
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|devMinor
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|name
operator|=
operator|new
name|StringBuffer
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|isDir
condition|?
name|DEFAULT_DIR_MODE
else|:
name|DEFAULT_FILE_MODE
expr_stmt|;
name|this
operator|.
name|linkFlag
operator|=
name|isDir
condition|?
name|LF_DIR
else|:
name|LF_NORMAL
expr_stmt|;
name|this
operator|.
name|userId
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|groupId
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|size
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|modTime
operator|=
operator|(
operator|new
name|Date
argument_list|()
operator|)
operator|.
name|getTime
argument_list|()
operator|/
name|MILLIS_PER_SECOND
expr_stmt|;
name|this
operator|.
name|linkName
operator|=
operator|new
name|StringBuffer
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|userName
operator|=
operator|new
name|StringBuffer
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupName
operator|=
operator|new
name|StringBuffer
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|devMajor
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|devMinor
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Construct an entry with a name and a link flag.      *      * @param name the entry name      * @param linkFlag the entry link flag.      */
specifier|public
name|TarEntry
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
name|linkFlag
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|linkFlag
operator|=
name|linkFlag
expr_stmt|;
if|if
condition|(
name|linkFlag
operator|==
name|LF_GNUTYPE_LONGNAME
condition|)
block|{
name|magic
operator|=
operator|new
name|StringBuffer
argument_list|(
name|GNU_TMAGIC
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Construct an entry for a file. File is set to file, and the      * header is constructed from information from the file.      *      * @param file The file that the entry represents.      */
specifier|public
name|TarEntry
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|String
name|fileName
init|=
name|normalizeFileName
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|this
operator|.
name|linkName
operator|=
operator|new
name|StringBuffer
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
operator|new
name|StringBuffer
argument_list|(
name|fileName
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
name|this
operator|.
name|mode
operator|=
name|DEFAULT_DIR_MODE
expr_stmt|;
name|this
operator|.
name|linkFlag
operator|=
name|LF_DIR
expr_stmt|;
name|int
name|nameLength
init|=
name|name
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|nameLength
operator|==
literal|0
operator|||
name|name
operator|.
name|charAt
argument_list|(
name|nameLength
operator|-
literal|1
argument_list|)
operator|!=
literal|'/'
condition|)
block|{
name|this
operator|.
name|name
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|size
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|mode
operator|=
name|DEFAULT_FILE_MODE
expr_stmt|;
name|this
operator|.
name|linkFlag
operator|=
name|LF_NORMAL
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|modTime
operator|=
name|file
operator|.
name|lastModified
argument_list|()
operator|/
name|MILLIS_PER_SECOND
expr_stmt|;
name|this
operator|.
name|devMajor
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|devMinor
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Construct an entry from an archive's header bytes. File is set      * to null.      *      * @param headerBuf The header bytes from a tar archive entry.      */
specifier|public
name|TarEntry
parameter_list|(
name|byte
index|[]
name|headerBuf
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|parseTarHeader
argument_list|(
name|headerBuf
argument_list|)
expr_stmt|;
block|}
comment|/**      * Determine if the two entries are equal. Equality is determined      * by the header names being equal.      *      * @param it Entry to be checked for equality.      * @return True if the entries are equal.      */
specifier|public
name|boolean
name|equals
parameter_list|(
name|TarEntry
name|it
parameter_list|)
block|{
return|return
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|it
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Determine if the two entries are equal. Equality is determined      * by the header names being equal.      *      * @param it Entry to be checked for equality.      * @return True if the entries are equal.      */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|it
parameter_list|)
block|{
if|if
condition|(
name|it
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|it
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|equals
argument_list|(
operator|(
name|TarEntry
operator|)
name|it
argument_list|)
return|;
block|}
comment|/**      * Hashcodes are based on entry names.      *      * @return the entry hashcode      */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * Determine if the given entry is a descendant of this entry.      * Descendancy is determined by the name of the descendant      * starting with this entry's name.      *      * @param desc Entry to be checked as a descendent of this.      * @return True if entry is a descendant of this.      */
specifier|public
name|boolean
name|isDescendent
parameter_list|(
name|TarEntry
name|desc
parameter_list|)
block|{
return|return
name|desc
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Get this entry's name.      *      * @return This entry's name.      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Set this entry's name.      *      * @param name This entry's new name.      */
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
operator|new
name|StringBuffer
argument_list|(
name|normalizeFileName
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the mode for this entry      *      * @param mode the mode for this entry      */
specifier|public
name|void
name|setMode
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
comment|/**      * Get this entry's link name.      *      * @return This entry's link name.      */
specifier|public
name|String
name|getLinkName
parameter_list|()
block|{
return|return
name|linkName
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Get this entry's user id.      *      * @return This entry's user id.      */
specifier|public
name|int
name|getUserId
parameter_list|()
block|{
return|return
name|userId
return|;
block|}
comment|/**      * Set this entry's user id.      *      * @param userId This entry's new user id.      */
specifier|public
name|void
name|setUserId
parameter_list|(
name|int
name|userId
parameter_list|)
block|{
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
block|}
comment|/**      * Get this entry's group id.      *      * @return This entry's group id.      */
specifier|public
name|int
name|getGroupId
parameter_list|()
block|{
return|return
name|groupId
return|;
block|}
comment|/**      * Set this entry's group id.      *      * @param groupId This entry's new group id.      */
specifier|public
name|void
name|setGroupId
parameter_list|(
name|int
name|groupId
parameter_list|)
block|{
name|this
operator|.
name|groupId
operator|=
name|groupId
expr_stmt|;
block|}
comment|/**      * Get this entry's user name.      *      * @return This entry's user name.      */
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Set this entry's user name.      *      * @param userName This entry's new user name.      */
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
operator|new
name|StringBuffer
argument_list|(
name|userName
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get this entry's group name.      *      * @return This entry's group name.      */
specifier|public
name|String
name|getGroupName
parameter_list|()
block|{
return|return
name|groupName
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Set this entry's group name.      *      * @param groupName This entry's new group name.      */
specifier|public
name|void
name|setGroupName
parameter_list|(
name|String
name|groupName
parameter_list|)
block|{
name|this
operator|.
name|groupName
operator|=
operator|new
name|StringBuffer
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
block|}
comment|/**      * Convenience method to set this entry's group and user ids.      *      * @param userId This entry's new user id.      * @param groupId This entry's new group id.      */
specifier|public
name|void
name|setIds
parameter_list|(
name|int
name|userId
parameter_list|,
name|int
name|groupId
parameter_list|)
block|{
name|setUserId
argument_list|(
name|userId
argument_list|)
expr_stmt|;
name|setGroupId
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Convenience method to set this entry's group and user names.      *      * @param userName This entry's new user name.      * @param groupName This entry's new group name.      */
specifier|public
name|void
name|setNames
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|groupName
parameter_list|)
block|{
name|setUserName
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|setGroupName
argument_list|(
name|groupName
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set this entry's modification time. The parameter passed      * to this method is in "Java time".      *      * @param time This entry's new modification time.      */
specifier|public
name|void
name|setModTime
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|modTime
operator|=
name|time
operator|/
name|MILLIS_PER_SECOND
expr_stmt|;
block|}
comment|/**      * Set this entry's modification time.      *      * @param time This entry's new modification time.      */
specifier|public
name|void
name|setModTime
parameter_list|(
name|Date
name|time
parameter_list|)
block|{
name|modTime
operator|=
name|time
operator|.
name|getTime
argument_list|()
operator|/
name|MILLIS_PER_SECOND
expr_stmt|;
block|}
comment|/**      * Set this entry's modification time.      *      * @return time This entry's new modification time.      */
specifier|public
name|Date
name|getModTime
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|(
name|modTime
operator|*
name|MILLIS_PER_SECOND
argument_list|)
return|;
block|}
comment|/**      * Get this entry's file.      *      * @return This entry's file.      */
specifier|public
name|File
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
comment|/**      * Get this entry's mode.      *      * @return This entry's mode.      */
specifier|public
name|int
name|getMode
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
comment|/**      * Get this entry's file size.      *      * @return This entry's file size.      */
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**      * Set this entry's file size.      *      * @param size This entry's new file size.      */
specifier|public
name|void
name|setSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/**      * Indicate if this entry is a GNU long name block      *      * @return true if this is a long name extension provided by GNU tar      */
specifier|public
name|boolean
name|isGNULongNameEntry
parameter_list|()
block|{
return|return
name|linkFlag
operator|==
name|LF_GNUTYPE_LONGNAME
operator|&&
name|name
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|GNU_LONGLINK
argument_list|)
return|;
block|}
comment|/**      * Return whether or not this entry represents a directory.      *      * @return True if this entry is a directory.      */
specifier|public
name|boolean
name|isDirectory
parameter_list|()
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
return|return
name|file
operator|.
name|isDirectory
argument_list|()
return|;
block|}
if|if
condition|(
name|linkFlag
operator|==
name|LF_DIR
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * If this entry represents a file, and the file is a directory, return      * an array of TarEntries for this entry's children.      *      * @return An array of TarEntry's for this entry's children.      */
specifier|public
name|TarEntry
index|[]
name|getDirectoryEntries
parameter_list|()
block|{
if|if
condition|(
name|file
operator|==
literal|null
operator|||
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
operator|new
name|TarEntry
index|[
literal|0
index|]
return|;
block|}
name|String
index|[]
name|list
init|=
name|file
operator|.
name|list
argument_list|()
decl_stmt|;
name|TarEntry
index|[]
name|result
init|=
operator|new
name|TarEntry
index|[
name|list
operator|.
name|length
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
name|list
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
operator|new
name|TarEntry
argument_list|(
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|list
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Write an entry's header information to a header buffer.      *      * @param outbuf The tar entry header buffer to fill in.      */
specifier|public
name|void
name|writeEntryHeader
parameter_list|(
name|byte
index|[]
name|outbuf
parameter_list|)
block|{
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getNameBytes
argument_list|(
name|name
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|NAMELEN
argument_list|)
expr_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getOctalBytes
argument_list|(
name|mode
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|MODELEN
argument_list|)
expr_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getOctalBytes
argument_list|(
name|userId
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|UIDLEN
argument_list|)
expr_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getOctalBytes
argument_list|(
name|groupId
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|GIDLEN
argument_list|)
expr_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getLongOctalBytes
argument_list|(
name|size
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|SIZELEN
argument_list|)
expr_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getLongOctalBytes
argument_list|(
name|modTime
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|MODTIMELEN
argument_list|)
expr_stmt|;
name|int
name|csOffset
init|=
name|offset
decl_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|CHKSUMLEN
condition|;
operator|++
name|c
control|)
block|{
name|outbuf
index|[
name|offset
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
literal|' '
expr_stmt|;
block|}
name|outbuf
index|[
name|offset
operator|++
index|]
operator|=
name|linkFlag
expr_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getNameBytes
argument_list|(
name|linkName
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|NAMELEN
argument_list|)
expr_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getNameBytes
argument_list|(
name|magic
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|MAGICLEN
argument_list|)
expr_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getNameBytes
argument_list|(
name|userName
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|UNAMELEN
argument_list|)
expr_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getNameBytes
argument_list|(
name|groupName
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|GNAMELEN
argument_list|)
expr_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getOctalBytes
argument_list|(
name|devMajor
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|DEVLEN
argument_list|)
expr_stmt|;
name|offset
operator|=
name|TarUtils
operator|.
name|getOctalBytes
argument_list|(
name|devMinor
argument_list|,
name|outbuf
argument_list|,
name|offset
argument_list|,
name|DEVLEN
argument_list|)
expr_stmt|;
while|while
condition|(
name|offset
operator|<
name|outbuf
operator|.
name|length
condition|)
block|{
name|outbuf
index|[
name|offset
operator|++
index|]
operator|=
literal|0
expr_stmt|;
block|}
name|long
name|chk
init|=
name|TarUtils
operator|.
name|computeCheckSum
argument_list|(
name|outbuf
argument_list|)
decl_stmt|;
name|TarUtils
operator|.
name|getCheckSumOctalBytes
argument_list|(
name|chk
argument_list|,
name|outbuf
argument_list|,
name|csOffset
argument_list|,
name|CHKSUMLEN
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parse an entry's header information from a header buffer.      *      * @param header The tar entry header buffer to get information from.      */
specifier|public
name|void
name|parseTarHeader
parameter_list|(
name|byte
index|[]
name|header
parameter_list|)
block|{
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|name
operator|=
name|TarUtils
operator|.
name|parseName
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|NAMELEN
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|NAMELEN
expr_stmt|;
name|mode
operator|=
operator|(
name|int
operator|)
name|TarUtils
operator|.
name|parseOctal
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|MODELEN
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|MODELEN
expr_stmt|;
name|userId
operator|=
operator|(
name|int
operator|)
name|TarUtils
operator|.
name|parseOctal
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|UIDLEN
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|UIDLEN
expr_stmt|;
name|groupId
operator|=
operator|(
name|int
operator|)
name|TarUtils
operator|.
name|parseOctal
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|GIDLEN
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|GIDLEN
expr_stmt|;
name|size
operator|=
name|TarUtils
operator|.
name|parseOctal
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|SIZELEN
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|SIZELEN
expr_stmt|;
name|modTime
operator|=
name|TarUtils
operator|.
name|parseOctal
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|MODTIMELEN
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|MODTIMELEN
expr_stmt|;
name|offset
operator|+=
name|CHKSUMLEN
expr_stmt|;
name|linkFlag
operator|=
name|header
index|[
name|offset
operator|++
index|]
expr_stmt|;
name|linkName
operator|=
name|TarUtils
operator|.
name|parseName
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|NAMELEN
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|NAMELEN
expr_stmt|;
name|magic
operator|=
name|TarUtils
operator|.
name|parseName
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|MAGICLEN
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|MAGICLEN
expr_stmt|;
name|userName
operator|=
name|TarUtils
operator|.
name|parseName
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|UNAMELEN
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|UNAMELEN
expr_stmt|;
name|groupName
operator|=
name|TarUtils
operator|.
name|parseName
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|GNAMELEN
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|GNAMELEN
expr_stmt|;
name|devMajor
operator|=
operator|(
name|int
operator|)
name|TarUtils
operator|.
name|parseOctal
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|DEVLEN
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|DEVLEN
expr_stmt|;
name|devMinor
operator|=
operator|(
name|int
operator|)
name|TarUtils
operator|.
name|parseOctal
argument_list|(
name|header
argument_list|,
name|offset
argument_list|,
name|DEVLEN
argument_list|)
expr_stmt|;
block|}
comment|/**      * Strips Windows' drive letter as well as any leading slashes,      * turns path separators into forward slahes.      */
specifier|private
specifier|static
name|String
name|normalizeFileName
parameter_list|(
name|String
name|fileName
parameter_list|,
name|boolean
name|preserveLeadingSlashes
parameter_list|)
block|{
name|String
name|osname
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
if|if
condition|(
name|osname
operator|!=
literal|null
condition|)
block|{
comment|// Strip off drive letters!
comment|// REVIEW Would a better check be "(File.separator == '\')"?
if|if
condition|(
name|osname
operator|.
name|startsWith
argument_list|(
literal|"windows"
argument_list|)
condition|)
block|{
if|if
condition|(
name|fileName
operator|.
name|length
argument_list|()
operator|>
literal|2
condition|)
block|{
name|char
name|ch1
init|=
name|fileName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|char
name|ch2
init|=
name|fileName
operator|.
name|charAt
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch2
operator|==
literal|':'
operator|&&
operator|(
operator|(
name|ch1
operator|>=
literal|'a'
operator|&&
name|ch1
operator|<=
literal|'z'
operator|)
operator|||
operator|(
name|ch1
operator|>=
literal|'A'
operator|&&
name|ch1
operator|<=
literal|'Z'
operator|)
operator|)
condition|)
block|{
name|fileName
operator|=
name|fileName
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|osname
operator|.
name|indexOf
argument_list|(
literal|"netware"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|int
name|colon
init|=
name|fileName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|!=
operator|-
literal|1
condition|)
block|{
name|fileName
operator|=
name|fileName
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|fileName
operator|=
name|fileName
operator|.
name|replace
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
comment|// No absolute pathnames
comment|// Windows (and Posix?) paths can start with "\\NetworkDrive\",
comment|// so we loop on starting /'s.
while|while
condition|(
operator|!
name|preserveLeadingSlashes
operator|&&
name|fileName
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|fileName
operator|=
name|fileName
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|fileName
return|;
block|}
block|}
end_class

end_unit
