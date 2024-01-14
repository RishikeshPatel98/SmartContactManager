package com.smart.smartcontactmanager.controller;

import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.entities.contactDetails;
import com.smart.smartcontactmanager.helper.Message;
import com.smart.smartcontactmanager.repositories.ContactDetailsRepository;
import com.smart.smartcontactmanager.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactDetailsRepository contactDetailsRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    // method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        System.out.println("USERNAME " + userName);

        // get the user using usernamne(Email)

        User user = userRepository.getUserByUserName(userName);
        System.out.println("USER " + user);
        model.addAttribute("user", user);

    }
    // dashboard home
    @GetMapping("/index")
    public String dashboard(Model model, Principal principal)
    {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";

    }

    @GetMapping("/logout")
    public String logout() {
        // Implement any additional logout logic if needed
        return "redirect:/signin?logout";
    }

    // open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new contactDetails());

        return "normal/add_contact_form";
    }

      //processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute contactDetails contact, @RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session){

        try {

            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            // processing and uploading file..

            if (file.isEmpty()) {
                // if the file is empty then try our message
                System.out.println("File is empty");
                contact.setImage("contact.png");

            } else {
                // file the file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is uploaded");

            }

            user.getContacts().add(contact);

            contact.setUser(user);

            this.userRepository.save(user);

            System.out.println("DATA " + contact);

            System.out.println("Added to data base");

            // message success.......
            session.setAttribute("message", new Message("Your contact is added !! Add more..", "success"));
        }catch (Exception e){
            System.out.println("ERROR " + e.getMessage());
            e.printStackTrace();
            // message error
            session.setAttribute("message", new Message("Some went wrong !! Try again..", "danger"));
        }

        return "normal/add_contact_form";
    }

    // show contacts handler
    // per page = 5[n]
    // current page = 0 [page]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page,Model model, Principal principal ){
        model.addAttribute("title", "Show User Contacts");
        // sending contact list
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        // currentPage-page
        // Contact Per page - 5
        Pageable pageable = PageRequest.of(page, 1);

        Page<contactDetails> contacts = this.contactDetailsRepository.findContactsByUser(user.getId(), pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }
    // showing particular contact details.
    @GetMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal){
        System.out.println("CID " + cId);

        Optional<contactDetails> contactOptional = this.contactDetailsRepository.findById(cId);
        contactDetails contact = contactOptional.get();
        //
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }


        return "normal/contact_detail";
    }
    // delete contact handler
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session){
        Optional<contactDetails> contactDetailsOptional = this.contactDetailsRepository.findById(cId);
        contactDetails contact = contactDetailsOptional.get();

        contact.setUser(null);
        this.contactDetailsRepository.delete(contact);

        session.setAttribute("message", new Message("Contact deleted succesfully...", "success"));
        return "redirect:/user/show-contacts/0";
    }

    // open update form handler
    @PostMapping(("/update-contact/{cid}"))
    public String updateForm(@PathVariable("cid") Integer cid,Model model){
        model.addAttribute("title","Update Contact");

        contactDetails contact = this.contactDetailsRepository.findById(cid).get();

        model.addAttribute("contact", contact);
        return "normal/update_form";

    }
    // update contact handler
    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute contactDetails contact,
                                @RequestParam("profileImage") MultipartFile file,
                                Model model, HttpSession session, Principal principal){
        try {

            // old contact details
            contactDetails oldcontactDetail = this.contactDetailsRepository.findById(contact.getcId()).get();

            if (!file.isEmpty()){
                // file work..
                // rewrite

              //  delete old photo

                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile, oldcontactDetail.getImage());
                file1.delete();

              //  update new photo
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(file.getOriginalFilename());

            }else {
                contact.setImage(oldcontactDetail.getImage());
            }
            User user = this.userRepository.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactDetailsRepository.save(contact);

            session.setAttribute("message", new Message("Your contact is updated...", "success"));

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("CONTACT NAME " + contact.getName());
        System.out.println("CONTACT ID " + contact.getcId());
        return "redirect:/user/" + contact.getcId() + "/contact";
    }
    // your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model) {
        model.addAttribute("title", "Profile Page");
        return "normal/profile";
    }
    // open settings handler
    @GetMapping("/settings")
    public String openSettings() {
        return "normal/settings";
    }

    // change password..handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal, HttpSession session){

        System.out.println("OLD PASSWORD " + oldPassword);
        System.out.println("NEW PASSWORD " + newPassword);

        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);
        System.out.println(currentUser.getPassword());

        if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
            // change the password

            currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message", new Message("Your password is successfully changed..", "success"));

        } else {
            // error...
            session.setAttribute("message", new Message("Please Enter correct old password !!", "danger"));
            return "redirect:/user/settings";
        }
        return "redirect:/user/index";
    }
}
